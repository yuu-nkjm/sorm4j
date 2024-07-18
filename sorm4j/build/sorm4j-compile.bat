setlocal
cd /d %~dp0
call mvn-caller.bat "mvn clean compile"
endlocal
