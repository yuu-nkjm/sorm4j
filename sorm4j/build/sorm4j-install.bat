setlocal
cd /d %~dp0
killall java
call mvn-caller.bat "mvn clean install"
call sorm4j-check.bat
