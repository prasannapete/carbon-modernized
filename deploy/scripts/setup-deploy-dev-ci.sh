#!/usr/bin/env bash
# One-time setup for .github/workflows/deploy-dev.yml. Run it yourself, with your own
# carbon cluster admin context and gh login; it handles credentials, so it never prints them.
#
#   deploy/scripts/setup-deploy-dev-ci.sh
#
# 1. applies deploy/k8s/ci-rbac (ServiceAccount + srl-dev Role + token; nothing in srl-prod)
# 2. builds a kubeconfig for that ServiceAccount
# 3. creates the GitHub environment "dev", deployable from main only
# 4. stores SRL_DEV_KUBECONFIG and DOCKERHUB_USERNAME as dev environment secrets
# 5. prompts you for DOCKERHUB_TOKEN (a Docker Hub access token with write access to the
#    prasannapete/srl-* repositories)
#
# Re-running is safe; it refreshes the kubeconfig secret. To rotate the cluster token,
# delete secret/carbon-modernized-deployer-token in srl-dev first.
set -euo pipefail

ADMIN_CONTEXT=${ADMIN_CONTEXT:-carbon}
NS=srl-dev
SA=carbon-modernized-deployer
CI_CONTEXT=carbon-srl-dev-deployer
REPO=${REPO:-prasannapete/carbon-modernized}
ENVIRONMENT=dev
ROOT=$(git rev-parse --show-toplevel)
RBAC="$ROOT/deploy/k8s/ci-rbac/srl-dev-deployer.yaml"

die() { echo "ABORT: $*" >&2; exit 1; }
k() { kubectl --context="$ADMIN_CONTEXT" "$@"; }

# The RBAC file must only ever create objects in srl-dev.
grep -E '^\s*namespace:' "$RBAC" | grep -vq "namespace: $NS\$" && die "$RBAC has objects outside $NS"

echo "==> Applying CI deployer RBAC to $NS on context $ADMIN_CONTEXT"
k apply -f "$RBAC"

echo "==> Waiting for the ServiceAccount token"
token=""
for _ in $(seq 1 30); do
  token=$(k -n "$NS" get secret "$SA-token" -o jsonpath='{.data.token}' 2>/dev/null | base64 --decode || true)
  [[ -n "$token" ]] && break
  sleep 2
done
[[ -n "$token" ]] || die "token for $SA was not populated"

server=$(k config view --raw --minify -o jsonpath='{.clusters[0].cluster.server}')
ca=$(k config view --raw --minify -o jsonpath='{.clusters[0].cluster.certificate-authority-data}')
[[ -n "$server" && -n "$ca" ]] || die "could not read server/CA from context $ADMIN_CONTEXT"

workdir=$(mktemp -d)
trap 'rm -rf "$workdir"' EXIT
(umask 077; cat >"$workdir/kubeconfig" <<EOF
apiVersion: v1
kind: Config
clusters:
  - name: carbon
    cluster:
      server: $server
      certificate-authority-data: $ca
users:
  - name: $SA
    user:
      token: $token
contexts:
  - name: $CI_CONTEXT
    context:
      cluster: carbon
      user: $SA
      namespace: $NS
current-context: $CI_CONTEXT
EOF
)

echo "==> Checking the new credential is limited to carbon-modernized in $NS"
kc() { KUBECONFIG="$workdir/kubeconfig" kubectl --context="$CI_CONTEXT" auth can-i "$@" 2>/dev/null || true; }
[[ $(kc patch deployment/srl-common-service -n "$NS") == yes ]] || die "deployer cannot patch srl-common-service"
[[ $(kc patch deployment/srl-playmobil-service -n "$NS") == no ]] || die "deployer can patch playmobil"
[[ $(kc get deployments -n srl-prod) == no ]] || die "deployer can read srl-prod"
[[ $(kc patch deployments -n srl-prod) == no ]] || die "deployer can write srl-prod"

echo "==> GitHub environment '$ENVIRONMENT' on $REPO (deployable from main only)"
gh api -X PUT "repos/$REPO/environments/$ENVIRONMENT" --input - >/dev/null <<'EOF'
{"deployment_branch_policy": {"protected_branches": false, "custom_branch_policies": true}}
EOF
if ! gh api "repos/$REPO/environments/$ENVIRONMENT/deployment-branch-policies" \
     -q '.branch_policies[].name' | grep -qx main; then
  gh api -X POST "repos/$REPO/environments/$ENVIRONMENT/deployment-branch-policies" \
    -f name=main -f type=branch >/dev/null
fi

echo "==> Storing secrets in environment '$ENVIRONMENT'"
gh secret set SRL_DEV_KUBECONFIG --repo "$REPO" --env "$ENVIRONMENT" <"$workdir/kubeconfig"
gh secret set DOCKERHUB_USERNAME --repo "$REPO" --env "$ENVIRONMENT" --body prasannapete

if gh secret list --repo "$REPO" --env "$ENVIRONMENT" | grep -q '^DOCKERHUB_TOKEN'; then
  echo "DOCKERHUB_TOKEN already set; leaving it."
else
  echo "Paste a Docker Hub access token for prasannapete (Read & Write); input is hidden:"
  gh secret set DOCKERHUB_TOKEN --repo "$REPO" --env "$ENVIRONMENT"
fi

echo "Done. Run the workflow from the Actions tab (Deploy to dev -> Run workflow) or push to main."
