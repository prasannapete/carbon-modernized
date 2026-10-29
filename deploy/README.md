# Deploying carbon-modernized to the `carbon` cluster (srl-dev)

## Never touched

- **Everything in `srl-prod`.** It runs `prasannapete/*:v0.0.1` images, and there is deliberately no prod overlay here.
- **`srl-playmobil-service`**, in both namespaces, and its image repo.
- **Shared objects in `srl-dev`:** `srl-config-map`, `srl-api-ingress` and every other Service or Ingress. The manifests don't include them.

`deploy/scripts/deploy-srl-dev.sh` enforces this. It always passes `--context=carbon` and `-n srl-dev`, which matters because the default kube context is another cluster. It also refuses manifests that:
- name another namespace,
- mention playmobil, or
- use a `latest` or `v0.0.1` image tag.

It snapshots `srl-playmobil-service` (pod UID, restart count, generation) and every `srl-prod` workload's images before applying, and fails if anything differs afterwards.

## What gets deployed

| Workload (srl-dev) | Kind | Source | Port |
|---|---|---|---|
| authserver | StatefulSet | services/authserver | 9201 |
| srl-common-service | Deployment | services/common-service | 9202 |
| srl-shell-analytics-service | Deployment | services/shell-analytics-service | 9203 |
| srl-kitkat-service | Deployment | services/kitkat-service | 9204 |
| srl-dashboard | Deployment | frontend/srldashboard (nginx) | 3000 |
| srl-leaderboard-web | Deployment | frontend/leaderboard-web (nginx) | 9111 |
| srl-web | Deployment | frontend/web (nginx) | 9204 |

Ports match the existing Services' `targetPort`s, so no Service or Ingress changes are needed.

### Configuration

The backends read config in this order, with later sources winning:
1. `srl-config-map` (shared, unchanged).
2. `carbon-modernized-config` (new, non-secret, in this repo).
3. Explicit `env` entries.

The explicit `env` entries map the property names this code uses onto the existing keys in `srl-config-map`:

| Property used by the code | Key in `srl-config-map` |
|---|---|
| `MAIL_USERNAME` | `MAIL_USER_NAME` |
| `SENDGRID_API_KEY` | `SEND_GRID_KEY` |
| `MAIL_FROM_EMAIL_ID` | `SRL_MAIL_FROM_EMAIL_ID` |
| `ACCESS_TOKEN_URL` | `ACCESS_TOKEN_URI` |
| `CLIENT_TOKEN_URL` | `Client_TOKEN_URL` |

### Frontends

The frontends use nginx to proxy API calls the same way the Vite dev server does, so the browser stays on one origin and no CORS changes are needed on the shared ingresses:
- **leaderboard-web:** `/api/` goes to `srl-kitkat-service-np`, and `/oauth2/token` goes to `srl-auth-server-service-np`.
- **web:** `/leaderboard/` goes to `srl-common-service-np`.

## Steps

```bash
# 1. Build and push images tagged with the git SHA (clean tree required for --push)
deploy/scripts/build-images.sh --push

# 2. Review what would change (read-only)
deploy/scripts/deploy-srl-dev.sh

# 3. Apply, wait for rollouts, verify protected workloads unchanged
deploy/scripts/deploy-srl-dev.sh --apply
```

**Rollback:** `kubectl --context=carbon -n srl-dev rollout undo deploy/<name>` (or `sts/authserver`). Each run also writes a full YAML backup to `~/.local/state/carbon-srl-dev-backups/`, readable only by you because it includes the credentials stored in `srl-config-map`.

## Before the first apply

- **OAuth redirect URI.** Register `https://leaderboard.prapticonsulting.com/oauth2/callback` on the `srl-analytics` client in the srl-dev authserver database. leaderboard-web now does its own authorization-code login.
- **Database migrations.** Review the `maintenance/db-patcher` changesets. The srl-dev database `srl_analytics_dev` is shared with `srl-playmobil-service`, so changes must be additive only.
- **Play Store reports.** `PLAYSTORE_JSON_FILE_PATH` is empty, so Play Store report fetching in shell-analytics won't work in srl-dev until a service-account JSON is mounted.
