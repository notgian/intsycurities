@echo off
if "%~1"=="" (
    echo Usage: %~nx0 ^<map name^> [--gui]
    exit /b 1
)
del /s /q *.class
javac src/main/Driver.java -cp src
set MODE=raw
if "%2"=="--gui" set MODE=bot
java -classpath src main.Driver %1 %MODE%