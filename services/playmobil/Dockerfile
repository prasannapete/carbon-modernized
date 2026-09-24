FROM alpine:3.20

RUN apk add --no-cache nginx openssh shadow && \
    mkdir -p /run/nginx /run/sshd /var/www/html /etc/nginx/http.d

ENV WWW_ROOT=/var/www/html \
    SFTP_USER=web \
    SFTP_PASSWORD=p@pth!c0ns82k26 \
    SFTP_UID=1000 \
    SFTP_GID=1000

COPY docker/nginx.conf /etc/nginx/nginx.conf
COPY docker/sshd_config /etc/ssh/sshd_config
COPY docker/entrypoint.sh /entrypoint.sh

RUN chmod +x /entrypoint.sh

EXPOSE 80 22

ENTRYPOINT ["/entrypoint.sh"]
