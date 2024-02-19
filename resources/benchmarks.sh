#!/bin/sh

# Directory containing the files
ROOT_DIR=".."

# Path to the JAR file
JAR_PATH="$ROOT_DIR/build/jar/DeltaDebugging-0.1.0.jar"

# Path to als models files
ALS_PATH="./als_models"

# Output directory for JFR recordings
OUTPUT_DIR="./jfr_output"

# Process each file
for FILE in "$ALS_PATH"/*.als
do
    # Generate a unique filename for the JFR recording
    JFR_FILENAME="$OUTPUT_DIR/$(basename "$FILE")-$(date +%s).jfr"

    # Start the Java application with JFR enabled
    java -XX:StartFlightRecording=filename="$JFR_FILENAME",settings=profile -jar "$JAR_PATH" -i "$FILE" -p -t &

    # Limit the number of concurrent processes, if necessary
    # Adjust the number 10 to your system's capacity
    while [ $(jobs | wc -l) -ge 10 ]; do
        sleep 1
    done
done

# Wait for all background processes to finish
wait

