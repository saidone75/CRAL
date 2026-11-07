@echo off
setlocal
cd /d "%~dp0"
set "COMPOSE_PROJECT_NAME=cral-integration-%RANDOM%-%RANDOM%"
set "RESULT_DIR=%CD%\integration-results\%COMPOSE_PROJECT_NAME%"
set "COMPOSE=docker compose --env-file docker/.env -f docker/docker-compose.yml -f docker/integration/compose.yml"
mkdir "%RESULT_DIR%"
%COMPOSE% config --quiet
if errorlevel 1 exit /b 1
%COMPOSE% up --build -d alfresco postgres solr6 activemq transform-core-aio
if errorlevel 1 goto failed
%COMPOSE% build integration
if errorlevel 1 goto failed
%COMPOSE% run --rm -T --no-deps integration >"%RESULT_DIR%\tests.log" 2>&1
set "RESULT=%ERRORLEVEL%"
type "%RESULT_DIR%\tests.log"
goto cleanup
:failed
set "RESULT=1"
:cleanup
%COMPOSE% logs --no-color >"%RESULT_DIR%\services.log" 2>&1
%COMPOSE% down --volumes --remove-orphans >"%RESULT_DIR%\cleanup.log" 2>&1
if errorlevel 1 (
    echo Cleanup failed; see "%RESULT_DIR%\cleanup.log"
    set "RESULT=1"
)
echo Integration test exit code: %RESULT%. Logs: "%RESULT_DIR%"
exit /b %RESULT%
