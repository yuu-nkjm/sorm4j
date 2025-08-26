@echo off
setlocal
chcp 65001
set _B=%~dp0
set BAT_DIR=%_B:~0,-1%

cd /d %BAT_DIR%\..\

@echo on
java -version
@echo off

:loop
if "%~1"=="" goto end
call %~1
shift
goto loop
:end

@if errorlevel 1 pause
endlocal
