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

**Rollback:** `kubectl --context=carbon -n srl-dev rollout undo deploy/<name>` (or `sts/authserver`), or redeploy an earlier commit's tag. Each apply also writes a YAML backup of the objects it manages (their state before the apply) to `~/.local/state/carbon-srl-dev-backups/`, owner-only.

## Automatic deploys (GitHub Actions)

`.github/workflows/deploy-dev.yml` runs on every push to `main` (Markdown-only and `maintenance/` changes excepted) and from **Actions → Deploy to dev → Run workflow**. It builds the 7 images, pushes them tagged with the commit's short SHA, then runs `deploy-srl-dev.sh` (diff, then `--apply`). Runs are queued, never run in parallel or cancelled mid-rollout.

Production cannot be reached from the workflow:

- The cluster credential is the `carbon-modernized-deployer` ServiceAccount (`deploy/k8s/ci-rbac/`). It can patch only the 7 carbon-modernized workloads and `carbon-modernized-config` in srl-dev, can read workloads and pods there, and has **no access to srl-prod**, `srl-playmobil-service`, `srl-config-map`, Services or Ingresses. It cannot create or delete anything. Nothing is created in srl-prod.
- An admission policy (`srl-dev-deployer-policy.yaml`, matched only to that ServiceAccount's requests) limits what its patches may put in a pod template: `prasannapete/srl-*` images at a commit SHA, nonprod nodes only, the default ServiceAccount, no volumes, Secrets, host access or privileged settings, and unchanged pod labels (so it can't take over another Service's traffic).
- A preflight step checks those limits with `kubectl auth can-i` and fails the run if the credential can do more.
- Images are built before the workflow logs in to Docker Hub, so build steps never see the registry token.
- Images are only ever tagged with a commit SHA; `latest`, `v0.0.1` and playmobil repositories are refused. The workflow records the `v0.0.1`/`latest` digests of the repos it pushes to and fails if they change during the run.
- Images stay on Docker Hub under `prasannapete`, where a personal access token can't be limited to some repositories: the CI token could technically write the `:v0.0.1` tags srl-prod pulls. The checks above make the workflow itself never do that; to make it impossible for the token too, turn on **immutable tags** (Docker Hub → repository → Settings) for `v0.0.1` on the production repositories.
- The deploy script's checks still apply. In CI it skips the srl-prod snapshot (`CHECK_SRL_PROD=false`) because the credential cannot read srl-prod.

Every deploy restarts all 7 workloads, including the authserver, whose signing keys are regenerated on start: anyone signed in to srl-dev (dashboard, leaderboard, srl-dev playmobil) has to sign in again.

**One-time setup** (needs your carbon cluster admin context and `gh` login; nothing is printed):

```bash
deploy/scripts/setup-deploy-dev-ci.sh
```

It applies `deploy/k8s/ci-rbac/` (policy and RBAC), builds a kubeconfig for the ServiceAccount, creates the `dev` environment (deployable from `main` only), stores `SRL_DEV_KUBECONFIG` and `DOCKERHUB_USERNAME` there, and prompts for `DOCKERHUB_TOKEN`: a Docker Hub personal access token for `prasannapete` with **Read & Write** only (not Delete). To revoke CI access, delete `secret/carbon-modernized-deployer-token` (or the ServiceAccount) in srl-dev.

## Before the first apply

- **OAuth redirect URI.** Done 2026-10-29: `https://leaderboard.prapticonsulting.com/oauth2/callback` was appended to the `srl-analytics` client's `redirect_uris` in `oauth2_registered_client` (existing URIs untouched; the old row is backed up under `~/.local/state/carbon-srl-dev-backups/`).
- **Database migrations.** Checked 2026-10-29: `srl_analytics_dev` already has everything the services map (all `@Table`s, `srl_tenant`, every `tenant_id` column from `bootstrap_carbon.sql`), and no service changes the schema at startup (no `ddl-auto`, Liquibase or Flyway). Nothing to run. Any future change must stay additive, because `srl-playmobil-service` shares this database.
- **CORS origins.** The authserver and shell-analytics enforce CORS themselves, only for `AUTH_ALLOWED_ORIGINS` (for the authserver also the post-logout redirect allowlist). Keep that list in `carbon-modernized-config` identical to the `cors-allow-origin` annotation on srl-dev's `srl-api-ingress` and `srl-auth-server-np-ingress`, which answer the browser preflights.
- **Play Store reports.** `PLAYSTORE_JSON_FILE_PATH` is empty, so Play Store report fetching in shell-analytics won't work in srl-dev until a service-account JSON is mounted.
