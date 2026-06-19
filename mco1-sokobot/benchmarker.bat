@echo off
setlocal enabledelayedexpansion

del /s /q *.class >nul 2>&1

set benchmark_file=benchmarks
if exist %benchmark_file% del %benchmark_file%

set MAPS=threeboxes1 threeboxes2 threeboxes3 fourboxes1 fourboxes2 fourboxes3 fiveboxes1 fiveboxes2 fiveboxes3 original1 original2 original3 testlevel

javac src\main\Driver.java -cp src

for %%m in (%MAPS%) do (
    java -classpath src main.Driver %%m check
    set "result="
    if exist temp.txt (
        set /p result=<temp.txt
    )
    echo %%m: !result!>> %benchmark_file%
)

if exist temp.txt del temp.txt
echo Benchmarks complete. Results stored in %benchmark_file%
type %benchmark_file%

endlocal
