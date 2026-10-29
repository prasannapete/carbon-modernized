#!/usr/bin/env bash
# Build the carbon-modernized images for srl-dev, tagged with the git SHA.
#   deploy/scripts/build-images.sh           # build only (local)
#   deploy/scripts/build-images.sh --push    # build and push to Docker Hub
#   deploy/scripts/build-images.sh --push-only  # push images already built (CI logs in to
#                                               # the registry only after building)
#
# Guardrails: only the 7 images below, only git-SHA tags. Never pushes latest or v0.0.1
# (srl-prod runs :v0.0.1) and never touches the srl-playmobil-service repo.
set -euo pipefail

ROOT=$(git rev-parse --show-toplevel)
cd "$ROOT"
BUILD=true
PUSH=false
LIST=false
case "${1:-}" in
  --push) PUSH=true ;;
  --push-only) BUILD=false; PUSH=true ;;
  --list-repos) LIST=true ;;  # print the image repositories, one per line
esac

TAG=$(git rev-parse --short=7 HEAD)
if $PUSH && [[ -n "$(git status --porcelain)" ]]; then
  echo "Refusing to push: working tree is dirty, so $TAG would not match the code." >&2
  exit 1
fi

# image repo | build context | build args
IMAGES=(
  "prasannapete/srl-auth-server|services/authserver|"
  "prasannapete/srl-common-service|services/common-service|"
  "prasannapete/srl-shell-analytics-service|services/shell-analytics-service|"
  "prasannapete/srl-kitkat-service|services/kitkat-service|"
  "prasannapete/srl-dashboard|frontend/srldashboard|ENV_FILE=.env-dev"
  "prasannapete/srl-leaderboard-web-application|frontend/leaderboard-web|ENV_FILE=.env-dev"
  "prasannapete/srl-web-application|frontend/web|"
)

if $LIST; then
  for entry in "${IMAGES[@]}"; do echo "${entry%%|*}"; done
  exit 0
fi

for entry in "${IMAGES[@]}"; do
  repo=${entry%%|*}
  if [[ "$repo" == *playmobil* || "$TAG" == "latest" || "$TAG" == v0.0.1 ]]; then
    echo "Refusing to build $repo:$TAG" >&2
    exit 1
  fi
done

if $BUILD; then
  # The services use pcpl-sdk as a system-scope jar from library/jars (not in git), which
  # Maven doesn't treat as a build dependency, so build it first or a clean checkout fails.
  sh services/authserver/mvnw -q -f "$ROOT/pom.xml" -pl library/pcpl-sdk -DskipTests package
  sh services/authserver/mvnw -q -f "$ROOT/pom.xml" -pl services/authserver,services/common-service,services/shell-analytics-service,services/kitkat-service \
    -am -DskipTests package

  for entry in "${IMAGES[@]}"; do
    IFS='|' read -r repo context buildarg <<<"$entry"
    args=(--platform linux/amd64 -t "$repo:$TAG")
    [[ -n "$buildarg" ]] && args+=(--build-arg "$buildarg")
    echo "==> $repo:$TAG ($context)"
    docker buildx build "${args[@]}" --load "$context"
  done
  echo "Built tag $TAG"
fi

if $PUSH; then
  for entry in "${IMAGES[@]}"; do
    repo=${entry%%|*}
    docker push "$repo:$TAG"
  done
  echo "Pushed tag $TAG"
fi
