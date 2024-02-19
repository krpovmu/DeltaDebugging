#!/bin/bash

# Path to the directory with JFR files
JFR_DIRECTORY="./jfr_output"

# Output file for aggregated data
OUTPUT_FILE="performance_costs_summary.csv"

# Header for the CSV file
echo "FileName,CPUTime,HeapUsage,GC_TotalPauseTime,ConcurrencyCost,IOWritten,IOWrittenDuration,IORead,IOReadDuration" > "$OUTPUT_FILE"

# Loop through JFR files and extract data
for JFR_FILE in "$JFR_DIRECTORY"/*.jfr
do
    CPUTime=$(jfr print --events jdk.CPULoad "$JFR_FILE" | grep "machineTotal" | sed 's/ //g' | sed '/^$/d' | cut -d'=' -f2 | awk '{sum += $1; n++} END {if(n > 0) print sum / n "%"}')
    HeapUsage=$(jfr print --events jdk.GCHeapSummary "$JFR_FILE" | grep "heapUsed" | sed 's/ //g' | sed '/^$/d' | cut -d'=' -f2 | awk '{sum += $1} END {print sum "MB"}')
    
    #Latency
    GC_TotalPauseTime=$(jfr print --events jdk.GCPhasePause "$JFR_FILE" | grep "duration"  | sed 's/ //g' | sed '/^$/d' | cut -d'=' -f2 | awk '{sum += $1} END {print sum}')

    #Concurrency costs
    ConcurrencyCost=$(jfr print --events JavaMonitorWait "$JFR_FILE" | grep "duration" | sed 's/ //g' | sed '/^$/d' | cut -d"=" -f2 | awk '{sum += $0;++n} END {if(n > 0) print sum/n "ms"}')

    #io written
    io_written=$(jfr print --events jdk.FileWrite "$JFR_FILE"| grep "bytesWritten" | sed 's/ //g' | cut -d"=" -f2 | awk '{sum += $1} END {print sum "bytes"}')
    io_written_duration=$(jfr print --events jdk.FileWrite "$JFR_FILE"| grep "duration" | sed 's/ //g' | cut -d"=" -f2 | awk '{sum += $1} END {print sum "ms"}')

    #io read
    io_read=$(jfr print --events jdk.FileRead "$JFR_FILE"| grep "bytesRead" | sed 's/ //g' | cut -d"=" -f2 | awk '{sum += $1} END {print sum "bytes"}')
    io_read_duration=$(jfr print --events jdk.FileRead "$JFR_FILE"| grep "duration" | sed 's/ //g' | cut -d"=" -f2 | awk '{sum += $1} END {print sum "ms"}')

    # Append data to the CSV file
    echo "$(basename "$JFR_FILE"),$CPUTime,$HeapUsage,$GC_TotalPauseTime,$ConcurrencyCost,$io_written,$io_written_duration,$io_read,$io_read_duration" >> "$OUTPUT_FILE"
done

