setlocal
@echo off

set CURRENT_DIR=%~dp0
cd /d %CURRENT_DIR%
cd ../

call mvn versions:use-latest-versions

@if errorlevel 1 pause
endlocal
