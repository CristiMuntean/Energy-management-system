FROM mysql:latest

ENV MYSQL_ROOT_PASSWORD=passw0rd
ENV MYSQL_DATABASE=energy-management-device

COPY energy-management-device.sql /docker-entrypoint-initdb.d/0001-energy-management-device.sql

COPY my.cnf /etc/mysql/conf.d/