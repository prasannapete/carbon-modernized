#!/bin/sh
set -eu

WWW_ROOT="${WWW_ROOT:-/var/www/html}"
SFTP_USER="${SFTP_USER:-web}"
SFTP_PASSWORD="${SFTP_PASSWORD:-webpass}"
SFTP_UID="${SFTP_UID:-1000}"
SFTP_GID="${SFTP_GID:-1000}"

mkdir -p /run/nginx /run/sshd "$WWW_ROOT"

if ! grep -q "^${SFTP_USER}:" /etc/group; then
    addgroup -g "$SFTP_GID" "$SFTP_USER"
fi

if ! grep -q "^${SFTP_USER}:" /etc/passwd; then
    adduser -D -h "$WWW_ROOT" -s /sbin/nologin -G "$SFTP_USER" -u "$SFTP_UID" "$SFTP_USER"
fi

echo "${SFTP_USER}:${SFTP_PASSWORD}" | chpasswd
chown -R "$SFTP_USER:$SFTP_USER" "$WWW_ROOT"

if [ ! -f "$WWW_ROOT/index.html" ]; then
    cat >"$WWW_ROOT/index.html" <<'EOF'
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title>nginx + sftp</title>
</head>
<body>
  <h1>nginx is serving /var/www/html</h1>
  <p>Upload files over SFTP into this directory to publish them.</p>
</body>
</html>
EOF
    chown "$SFTP_USER:$SFTP_USER" "$WWW_ROOT/index.html"
fi

ssh-keygen -A
nginx
exec /usr/sbin/sshd -D -e
