# Build stage
FROM --platform=linux/amd64 node:22-alpine AS build
LABEL authors="sharanya"
WORKDIR /carbon
COPY package.json package-lock.json ./
RUN npm ci --legacy-peer-deps
COPY . .
RUN npm run build

# Runtime stage: nginx serves dist/ and does what the Vite dev proxy does
# (/leaderboard -> common-service). PORT matches the existing srl-web-np targetPort (9204).
FROM --platform=linux/amd64 nginx:1.27-alpine
ENV PORT=9204 \
    COMMON_UPSTREAM=http://srl-common-service-np
COPY deploy/nginx.conf.template /etc/nginx/templates/default.conf.template
COPY --from=build /carbon/dist /usr/share/nginx/html
EXPOSE 9204

# prasannapete/srl-web-application
