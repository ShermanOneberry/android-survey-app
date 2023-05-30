openssl req ^
-newkey rsa:4096 -nodes -sha256 -keyout certs/server.key ^
-addext "subjectAltName = IP:10.0.2.2, IP:127.0.0.1" ^
-x509 -days 365 -out certs/server.crt ^
-subj "/C=SG/ST=Singapore/L=Singapore/O=Oneberry/CN=registry"