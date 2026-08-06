#!/bin/bash

# Helper function to display usage instructions
usage() {
    echo "Usage: $0 <1|2|3>"
    echo "  1 : Zip mco1"
    echo "  2 : Zip mco2"
    echo "  3 : Zip mco3"
    exit 1
}

if [ "$#" -ne 1 ]; then
    echo "Error: Expected exactly 1 argument, but received $#." >&2
    usage
fi

CHOICE="$1"

case "$CHOICE" in
    1)
        FILENAME="mco1.zip"
        echo "Zipping up mco1 files..."
        find . -name "*.class" -type f -delete
        zip -r $FILENAME mco1-sokobot
        echo "file stored in $(pwd)/$FILENAME"
        ;;
    2)
        FILENAME="mco2.zip"
        echo "Zipping up mco2 files..."
        cd './mco2-pinoybot'

        zip -r $FILENAME .
        mv $FILENAME ..
        
        cd ..
        echo "file stored in $(pwd)/$FILENAME"
        ;;
    3)
        FILENAME="mco3.zip"
        MCO3_DIR="./mco3-catbot"
        # remove pycache
        rm -r "$MCO3_DIR/__pycache__"

        echo "Zipping up mco3 files..."
        zip -r $FILENAME $MCO3_DIR
        echo "file stored in $(pwd)/$FILENAME"
        ;;
    *)
        echo "Error: Invalid argument '$CHOICE'." >&2
        ;;
esac

exit 0
