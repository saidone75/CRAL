@ECHO OFF

SET COMPOSE_FILE_PATH=%CD%\docker\docker-compose.yml

IF [%1]==[] (
    echo "Usage: %0 {start|stop|purge|tail}"
    GOTO END
)

IF %1==start (
    CALL :start
    CALL :tail
    GOTO END
)
IF %1==stop (
    CALL :down
    GOTO END
)
IF %1==purge (
    CALL:down
    CALL:purge
    GOTO END
)
IF %1==tail (
    CALL :tail
    GOTO END
)

echo "Usage: %0 {start|stop|purge|tail}"
:END
EXIT /B %ERRORLEVEL%

:start
    docker volume create cral-acs-volume
    docker volume create cral-db-volume
    docker volume create cral-ass-volume
    docker-compose -f "%COMPOSE_FILE_PATH%" up --build -d
EXIT /B 0
:down
    if exist "%COMPOSE_FILE_PATH%" (
        docker-compose -f "%COMPOSE_FILE_PATH%" down
    )
EXIT /B 0
:tail
    docker-compose -f "%COMPOSE_FILE_PATH%" logs -f
EXIT /B 0
:purge
    docker volume rm -f cral-acs-volume
    docker volume rm -f cral-db-volume
    docker volume rm -f cral-ass-volume
