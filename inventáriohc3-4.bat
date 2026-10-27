@echo off
title Sistema de Inventario TI - HC3 / HC4
echo ====================================================
echo      INICIALIZANDO SISTEMA DE INVENTARIO TI (DOCKER)
echo ====================================================
echo.

cd /d C:\Users\1310458\IdeaProjects\inventario

echo [1/2] Subindo os Containers (MySQL + Java) no Docker...
docker-compose up -d --build
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERRO] O Docker Desktop nao esta rodando!
    pause
    exit /b
)

echo.
echo [2/2] Aguardando os servicos inicializarem...
echo O sistema estara disponivel no navegador em instantes...
echo.

:: Abre o navegador padrao apos 10 segundos
start "" timeout /t 10 ^&^& start http://localhost:8080

pause