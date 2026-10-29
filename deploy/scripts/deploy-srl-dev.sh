#!/usr/bin/env bash
# Deploy carbon-modernized to the carbon cluster, srl-dev namespace only.
#   deploy/scripts/deploy-srl-dev.sh [TAG]          # render + kubectl diff (no changes)
#   deploy/scripts/deploy-srl-dev.sh [TAG] --apply  # apply, wait, verify
# TAG defaults to the current git short SHA (the tag build-images.sh pushes).
#
# Guardrails:
#   - context and namespace are always explicit (the default context is another cluster)
#   - the rendered manifests must be srl-dev only, contain nothing playmobil, and use
#     no latest/v0.0.1 tags
#   - srl-playmobil-service (srl-dev) and every srl-prod workload are snapshotted before
#     and compared after; any difference fails the run
#
# Environment (used by the deploy-dev GitHub workflow):
#   KUBE_CONTEXT=<name>   kube context to use (default: carbon)
#   CHECK_SRL_PROD=false  skip the srl-prod snapshot. The CI deployer has no access to
#                         srl-prod at all (deploy/k8s/ci-rbac), which is the stronger guarantee.
set -euo pipefail

CONTEXT=${KUBE_CONTEXT:-carbon}
CHECK_SRL_PROD=${CHECK_SRL_PROD:-true}
NS=srl-dev
ROOT=$(git rev-parse --show-toplevel)
OVERLAY="$ROOT/deploy/k8s/srl-dev"
TAG=$(git -C "$ROOT" rev-parse --short=7 HEAD)
APPLY=false
for arg in "$@"; do
  case "$arg" in
    --apply) APPLY=true ;;
    *) TAG=$arg ;;
  esac
done

k() { kubectl --context="$CONTEXT" "$@"; }
die() { echo "ABORT: $*" >&2; exit 1; }

[[ "$TAG" =~ ^(latest|v0\.0\.1)$ ]] && die "tag '$TAG' is reserved (srl-prod runs v0.0.1)"

# Render with the tag substituted for GIT_SHA.
rendered=$(kubectl kustomize "$OVERLAY" | sed "s/:GIT_SHA\$/:$TAG/")

grep -q 'GIT_SHA' <<<"$rendered" && die "unsubstituted image tag"
# Nothing may mention playmobil, except the authserver's AUTH_ALLOWED_ORIGINS value,
# which keeps playmobil's existing origins allowed.
grep -vE '^\s*AUTH_ALLOWED_ORIGINS:' <<<"$rendered" | grep -iq 'playmobil' \
  && die "rendered manifests mention playmobil"
grep -E '^\s*namespace:' <<<"$rendered" | grep -vq "namespace: $NS\$" && die "object outside $NS"
grep -E 'image: .*:(latest|v0\.0\.1)$' <<<"$rendered" && die "reserved image tag in manifests"

snapshot() {
  # playmobil-service pod identity + restarts, and every srl-prod workload's images.
  k -n "$NS" get pods -l app=srl-playmobil-service \
    -o jsonpath='{range .items[*]}{.metadata.uid} {.status.containerStatuses[0].restartCount}{"\n"}{end}'
  k -n "$NS" get deploy srl-playmobil-service -o jsonpath='{.metadata.generation}{"\n"}'
  if [[ "$CHECK_SRL_PROD" == true ]]; then
    k -n srl-prod get deploy,sts \
      -o jsonpath='{range .items[*]}{.kind}/{.metadata.name} {.metadata.generation} {.spec.template.spec.containers[*].image}{"\n"}{end}'
  fi
}

before=$(snapshot)
echo "Protected workloads snapshot taken ($(wc -l <<<"$before") lines)."

if ! $APPLY; then
  echo "=== kubectl diff (no changes made) ==="
  kubectl --context="$CONTEXT" -n "$NS" diff -f - <<<"$rendered" || true
  exit 0
fi

# Backup of the objects this deploy manages (their live state before the apply). Kept
# outside the repo, owner-only. Override the location with BACKUP_DIR.
backup_dir="${BACKUP_DIR:-${XDG_STATE_HOME:-$HOME/.local/state}/carbon-srl-dev-backups}"
(umask 077; mkdir -p "$backup_dir")
backup="$backup_dir/srl-dev-$(date +%Y%m%d-%H%M%S).yaml"
(umask 077; k -n "$NS" get -f - -o yaml <<<"$rendered" >"$backup")
echo "Backup written to $backup"

k -n "$NS" apply -f - <<<"$rendered"
# Wait for the authserver first (the other services fetch its JWKS). Keep going on a
# failed rollout so the protected-workload check below always runs.
failed=()
k -n "$NS" rollout status sts/authserver --timeout=10m || failed+=(sts/authserver)
for d in srl-common-service srl-shell-analytics-service srl-kitkat-service \
         srl-dashboard srl-leaderboard-web srl-web; do
  k -n "$NS" rollout status "deploy/$d" --timeout=10m || failed+=("deploy/$d")
done

after=$(snapshot)
if [[ "$before" != "$after" ]]; then
  diff <(echo "$before") <(echo "$after") || true
  die "protected workloads changed during deploy - investigate now"
fi
echo "Protected workloads unchanged."
if (( ${#failed[@]} )); then
  die "rollout did not finish for: ${failed[*]} (roll back with: kubectl -n $NS rollout undo <name>)"
fi
echo "Deployed $TAG to $NS."
