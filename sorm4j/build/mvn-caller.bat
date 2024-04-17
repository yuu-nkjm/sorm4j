setlocal
chcp 65001
@echo off
set CURRENT_DIR=%~dp0
cd /d %CURRENT_DIR%
cd ../

set JAVA_HOME=%JAVA_17_HOME%
set PATH=%JAVA_HOME%/bin;%PATH%

@echo on
java -version
@echo off

call %~1

@if errorlevel 1 pause
endlocal
