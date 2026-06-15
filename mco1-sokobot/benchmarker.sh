#!/bin/bash
find . -name "*.class" -type f -delete
javac src/main/Driver.java -cp src

declare -a MAPS=("threeboxes1" "threeboxes2" "threeboxes3" "fourboxes1" "fourboxes2" "fourboxes3" "fiveboxes1" "fiveboxes2" "fiveboxes3" "original1" "original2" "original3" "testlevel")

benchmark_file='benchmarks'

if [ -f $benchmark_file ]; then
    rm $benchmark_file
fi;

for map in "${MAPS[@]}"
do
    java -classpath src main.Driver $map check
    result=$(cat temp.txt)
    # if [ -z "$result" ]; then result="FAILED!"; fi
    echo "$map: $result" >> benchmarks
done

rm "temp.txt"

echo "Benchmarks complete. Results stored in $benchmark_file"
cat $benchmark_file
