#!/bin/bash

# removes all .class files and zips the src folder
# via the dojo thingy

find . -name "*.class" -type f -delete
zip -r src.zip src
