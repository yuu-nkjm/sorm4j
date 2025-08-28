@echo off
chcp 65001 >nul
set _B=%~dp0
set BAT_DIR=%_B:~0,-1%
cd /d %BAT_DIR%
echo _____CHECK TEXT PETTERNS IN FILES_____
python pattern_search.py --skip ".html,.class,.js,.lastUpdated,.asc,.sha1,.repositories" --pattern "[亜-熙ぁ-んァ-ヶ]" ../target/*.jar
echo _____CHECK FILES INCLUDED_____
python jar_file_checker.py ../target/*.jar
pause
