#!/bin/bash
if [ -z "$1" ]; then
    echo "Usage: $0 <map name> [--gui]"
    exit 1
fi
find . -name "*.class" -type f -delete
javac src/main/Driver.java -cp src
MODE="raw"
if [ "$2" = "--gui" ]; then
    MODE="bot"
fi
java -classpath src main.Driver "$1" $MODE
