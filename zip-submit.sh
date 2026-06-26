#!/bin/bash

# removes all .class files and zips the folder for submission
# currently only for mco1

find . -name "*.class" -type f -delete
zip -r mco1.zip mco1-sokobot
