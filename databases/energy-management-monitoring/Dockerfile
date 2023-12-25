FROM mysql:latest

ENV MYSQL_ROOT_PASSWORD=passw0rd
ENV MYSQL_DATABASE=energy-management-monitoring

COPY energy-management-monitoring.sql /docker-entrypoint-initdb.d/0001-energy-management-monitoring.sql

COPY my.cnf /etc/mysql/conf.d/