@echo off
chcp 65001 >nul
set _B=%~dp0
set BAT_DIR=%_B:~0,-1%
cd /d %BAT_DIR%
set CHECK_ROOT_PATH=..\
set CHECK_RESOURCE_PATH=%CHECK_ROOT_PATH%\src\main\resource
set CHECK_TARGET_PATH=%CHECK_ROOT_PATH%\target
set CHECK_JAR_PATH=%CHECK_ROOT_PATH%target/*.jar
echo _____SIMPLE RESOURCE CHECK IN FILES_____
powershell.exe -ExecutionPolicy Bypass -File simple_dir_check.ps1 "%CHECK_RESOURCE_PATH%"
echo _____SIMPLE TARGET CHECK IN FILES_____
powershell.exe -ExecutionPolicy Bypass -File simple_dir_check.ps1 "%CHECK_TARGET_PATH%"
echo _____SIMPLE JAR CHECK IN FILES_____
powershell.exe -ExecutionPolicy Bypass -File simple_jar_check.ps1 "%CHECK_JAR_PATH%"
echo _____CHECK TEXT PETTERNS IN FILES_____
python pattern_search.py --skip ".html,.class,.js,.lastUpdated,.asc,.sha1,.repositories" --pattern "[亜-熙ぁ-んァ-ヶ]" %CHECK_JAR_PATH%
echo _____CHECK FILES INCLUDED_____
python jar_file_checker.py %CHECK_JAR_PATH%
pause
