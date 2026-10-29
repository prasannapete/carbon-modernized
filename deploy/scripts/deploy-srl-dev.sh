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
set -euo pipefail

CONTEXT=carbon
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
# No object, label, selector, image or config reference may point at playmobil. (Plain
# values may: AUTH_ALLOWED_ORIGINS keeps playmobil's existing origins allowed.)
grep -iqE '^\s*(-\s*)?(name|app|type|serviceName|image|secretName|claimName):.*playmobil' <<<"$rendered" \
  && die "rendered manifests reference a playmobil object"
grep -E '^\s*namespace:' <<<"$rendered" | grep -vq "namespace: $NS\$" && die "object outside $NS"
grep -E 'image: .*:(latest|v0\.0\.1)$' <<<"$rendered" && die "reserved image tag in manifests"

snapshot() {
  # playmobil-service pod identity + restarts, and every srl-prod workload's images.
  k -n "$NS" get pods -l app=srl-playmobil-service \
    -o jsonpath='{range .items[*]}{.metadata.uid} {.status.containerStatuses[0].restartCount}{"\n"}{end}'
  k -n "$NS" get deploy srl-playmobil-service -o jsonpath='{.metadata.generation}{"\n"}'
  k -n srl-prod get deploy,sts \
    -o jsonpath='{range .items[*]}{.kind}/{.metadata.name} {.metadata.generation} {.spec.template.spec.containers[*].image}{"\n"}{end}'
}

before=$(snapshot)
echo "Protected workloads snapshot taken ($(wc -l <<<"$before") lines)."

if ! $APPLY; then
  echo "=== kubectl diff (no changes made) ==="
  kubectl --context="$CONTEXT" -n "$NS" diff -f - <<<"$rendered" || true
  exit 0
fi

# srl-config-map holds credentials, so the backup lives outside the repo, owner-only.
backup_dir="${XDG_STATE_HOME:-$HOME/.local/state}/carbon-srl-dev-backups"
(umask 077; mkdir -p "$backup_dir")
backup="$backup_dir/srl-dev-$(date +%Y%m%d-%H%M%S).yaml"
(umask 077; k -n "$NS" get deploy,sts,cm -o yaml >"$backup")
echo "Backup written to $backup"

# authserver first: the other services fetch its JWKS.
k -n "$NS" apply -f - <<<"$rendered"
k -n "$NS" rollout status sts/authserver --timeout=10m
for d in srl-common-service srl-shell-analytics-service srl-kitkat-service \
         srl-dashboard srl-leaderboard-web srl-web; do
  k -n "$NS" rollout status "deploy/$d" --timeout=10m
done

after=$(snapshot)
if [[ "$before" != "$after" ]]; then
  diff <(echo "$before") <(echo "$after") || true
  die "protected workloads changed during deploy - investigate now"
fi
echo "Deployed $TAG to $NS. srl-playmobil-service and srl-prod unchanged."
