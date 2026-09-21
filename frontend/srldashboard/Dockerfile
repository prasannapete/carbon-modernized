FROM --platform=linux/amd64  node:lts
LABEL authors="telwyn"
EXPOSE 3000

WORKDIR /carbon
ADD  package-prod.json /carbon/package.json
ADD  .env /carbon/.env
RUN npm install --legacy-peer-deps
COPY . .

ENV DISABLE_ESLINT_PLUGIN=true
RUN CI=true npm run build
ENTRYPOINT ["npm", "start"]


# Runtime stage
#FROM nginx:1.27-alpine
#COPY deploy/nginx.conf /etc/nginx/conf.d/default.conf
#COPY --from=build /app/build /usr/share/nginx/html
#EXPOSE 80
#HEALTHCHECK CMD wget -qO- http://127.0.0.1/ || exit 1

 # prasannapete/srl-dashboard