# Build stage: Vite bakes VITE_* in at build time; the env file picks the target
# (.env-dev -> srl-dev auth URL).
FROM --platform=linux/amd64 node:22-alpine AS build
LABEL authors="sharanya"
WORKDIR /carbon
COPY package.json package-lock.json ./
RUN npm ci --legacy-peer-deps
COPY . .
ARG ENV_FILE=.env-dev
RUN cp "${ENV_FILE}" .env
RUN npm run build

# Runtime stage: nginx serves dist/ and does what the Vite dev proxy does
# (/api -> kitkat-service, /oauth2/token -> authserver), so the browser stays
# same-origin and needs no CORS changes on the shared ingresses.
# PORT matches the existing srl-leaderboard-web-np targetPort (9111).
FROM --platform=linux/amd64 nginx:1.27-alpine
ENV PORT=9111 \
    KITKAT_UPSTREAM=http://srl-kitkat-service-np \
    AUTH_UPSTREAM=http://srl-auth-server-service-np
COPY deploy/nginx.conf.template /etc/nginx/templates/default.conf.template
COPY --from=build /carbon/dist /usr/share/nginx/html
EXPOSE 9111

# prasannapete/srl-leaderboard-web-application
