@echo off

echo Stopping all running containers...
FOR /F "tokens=*" %%i IN ('docker ps -q') DO docker stop %%i

REM Remove all stopped containers
echo Removing all stopped containers...
FOR /F "tokens=*" %%i IN ('docker ps -a -q') DO docker rm %%i

REM Remove all images
echo Removing all images...
FOR /F "tokens=*" %%i IN ('docker images -q') DO docker rmi %%i

REM Remove all volumes
echo Removing all volumes...
docker volume prune -f

echo All containers, images, and volumes removed.
pause