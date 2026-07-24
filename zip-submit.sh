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
        TEMPDIR="mco2"
        echo "Zipping up mco2 files..."
        cd './mco2-pinoybot'

        zip -r $FILENAME .
        mv $FILENAME ..
        
        cd ..
        # mkdir $TEMPDIR
        # cp "./mco2-pinoybot/requirements.txt" $TEMPDIR
        # cp "./mco2-pinoybot/pinoybot.py" $TEMPDIR
        # cp "./mco2-pinoybot/extractor.py" $TEMPDIR
        # cp "./mco2-pinoybot/pinoybot_model_pipeline.pk1" $TEMPDIR
        # cd ./$TEMPDIR
        # zip -r $FILENAME *
        # mv $FILENAME ..
        # cd ..
        # rm -r $TEMPDIR
        echo "file stored in $(pwd)/$FILENAME"
        ;;
    3)
        echo "Not implemented yet..."
        ;;
    *)
        echo "Error: Invalid argument '$CHOICE'." >&2
        ;;
esac

exit 0
