setlocal
cd /d %~dp0
killall java
call mvn-caller.bat "mvn clean deploy -P ossrh"
call sorm4j-check.bat
endlocal
