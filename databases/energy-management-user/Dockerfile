FROM mysql:latest

ENV MYSQL_ROOT_PASSWORD=passw0rd
ENV MYSQL_DATABASE=energy-management-user

COPY energy-management-user.sql /docker-entrypoint-initdb.d/energy-management-user.sql

COPY my.cnf /etc/mysql/conf.d/