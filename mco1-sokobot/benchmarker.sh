#!/bin/bash
find . -name "*.class" -type f -delete
javac src/main/Driver.java -cp src

# declare -a MAPS=("threeboxes1" "threeboxes2" "threeboxes3" "fourboxes1" "fourboxes2" "fourboxes3" "fiveboxes1" "fiveboxes2" "fiveboxes3" "original1" "original2" "original3" "testlevel")
# declare -a MAPS=($(echo others/microban_{1..155}))
declare -a MAPS=($(echo others/nabokosmos_{1..40}))
# declare -a MAPS=($(echo others/microcosmos_{1..40}))

benchmark_file='benchmarks'

if [ -f $benchmark_file ]; then
    rm $benchmark_file
fi;

for map in "${MAPS[@]}"
do
    echo "current map: $map"
    java -classpath src main.Driver $map check
    result=$(cat temp.txt)
    # if [ -z "$result" ]; then result="FAILED!"; fi
    echo "$map: $result" >> benchmarks
done

rm "temp.txt"

echo ""
echo "Benchmarks complete. Results stored in $benchmark_file"
cat $benchmark_file
