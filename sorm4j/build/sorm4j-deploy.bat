setlocal
cd /d %~dp0
killall java
call mvn-caller.bat "mvn clean deploy -P ossrh"
endlocal
