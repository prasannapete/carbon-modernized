# Build stage: CRA bakes REACT_APP_* in at build time, so the env file picks the target
# (.env-dev -> srl-dev URLs). Uses package.json + lockfile (package-prod.json is stale
# and lacks the PDF/print deps the reports need).
FROM --platform=linux/amd64 node:22-alpine AS build
LABEL authors="sharanya"
WORKDIR /carbon
COPY package.json package-lock.json ./
RUN npm ci --legacy-peer-deps
COPY . .
ARG ENV_FILE=.env-dev
RUN cp "${ENV_FILE}" .env
ENV DISABLE_ESLINT_PLUGIN=true
RUN CI=false npm run build

# Runtime stage: static files behind nginx. PORT matches the existing srl-dashboard-np
# targetPort (3000) so the Service and Ingress don't change.
FROM --platform=linux/amd64 nginx:1.27-alpine
ENV PORT=3000
COPY deploy/nginx.conf.template /etc/nginx/templates/default.conf.template
COPY --from=build /carbon/build /usr/share/nginx/html
EXPOSE 3000

# prasannapete/srl-dashboard
