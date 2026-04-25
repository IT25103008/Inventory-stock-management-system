@echo off
setlocal EnableDelayedExpansion
title Papol OS - Development Server

:: Generate ESC character for ANSI colors safely
for /F %%a in ('echo prompt $E ^| cmd') do set "ESC=%%a"

set "WHT=%ESC%[97m"
set "GRY=%ESC%[90m"
set "RST=%ESC%[0m"
set "BLD=%ESC%[1m"
set "INV=%ESC%[7m"

set PORT=3000
set START_PAGE=html/login.html

cd /d "%~dp0"
cls

echo %GRY%=================================================================%RST%
echo  %INV% %WHT% PAPOL INVENTORY %RST% %BLD%%WHT% SYSTEM SERVER STARTUP %RST%
echo %GRY%=================================================================%RST%
echo.

:: 1. Force kill existing process on our port
echo %GRY%[%WHT%*%GRY%] Checking port %PORT% for ghost processes...%RST%
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :%PORT% ^| findstr LISTENING 2^>nul') do (
    if "%%a" neq "0" (
        echo %GRY%[%WHT%-%GRY%] Terminating old server ^(PID: %%a^)...%RST%
        taskkill /F /PID %%a >nul 2>&1
        timeout /t 1 /nobreak >nul
    )
)

echo %GRY%[%WHT%*%GRY%] Booting engine...%RST%

:: Determine best server to use
set SERVER_TYPE=none

where npx >nul 2>&1
if %errorlevel% equ 0 (
    set SERVER_TYPE=Node.js ^(http-server^)
    set RUN_CMD=npx -y http-server . -p %PORT% -c-1 --cors
    goto start_server
)

where python >nul 2>&1
if %errorlevel% equ 0 (
    set SERVER_TYPE=Python ^(http.server^)
    set RUN_CMD=python -m http.server %PORT% --bind 127.0.0.1
    goto start_server
)

where php >nul 2>&1
if %errorlevel% equ 0 (
    set SERVER_TYPE=PHP ^(built-in^)
    set RUN_CMD=php -S 127.0.0.1:%PORT%
    goto start_server
)

:start_server
if "%SERVER_TYPE%"=="none" (
    echo.
    echo %BLD%[ERROR] No suitable server environment found!%RST%
    echo %GRY%Please install Node.js ^(Recommended^) or Python, then try again.%RST%
    echo.
    pause
    exit /b 1
)

echo %GRY%[%WHT%+%GRY%] Engine selected: %WHT%%SERVER_TYPE%%RST%
echo %GRY%[%WHT%+%GRY%] Launching browser...%RST%

start http://127.0.0.1:%PORT%/%START_PAGE%

echo.
echo %GRY%-----------------------------------------------------------------%RST%
echo  %BLD%%WHT% SERVER ACTIVE %RST%
echo  %GRY%URL:    %RST%http://127.0.0.1:%PORT%/%START_PAGE%
echo  %GRY%Dir:    %RST%%~dp0
echo.
echo  %GRY%Press CTRL+C to shutdown gracefully.%RST%
echo %GRY%-----------------------------------------------------------------%RST%
echo.

call %RUN_CMD%

pause
