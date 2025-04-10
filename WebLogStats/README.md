# Web Log Stats Analyzer

A Java project for analyzing web server log files.

**Author:** Ron Inouye

## Description

This program reads web server log files, parses individual entries, and performs basic analysis, such as counting the number of unique IP addresses that have accessed the server.

## Components

The project currently consists of the following Java classes:

* **`LogEntry.java`**: A data class representing a single entry (record) from the log file. It stores information like IP address, access time, request details, status code, and bytes returned.
* **`WebLogParser.java`**: A utility class responsible for parsing a single line string from a log file (expected to be in Apache Common Log Format) into a structured `LogEntry` object. It handles date/time parsing.
* **`LogAnalyzer.java`**: The core class for analysis. It reads a specified log file line by line, uses `WebLogParser` to create `LogEntry` objects, stores these objects, and provides methods to analyze the stored data (e.g., `countUniqueIPs`).
* **`LogTester.java`**: The main entry point for the application. It creates a `LogAnalyzer` instance, specifies the log file to be processed, initiates the file reading, and runs tests or analysis methods (currently configured to count and print unique IPs from `short-test.log`).

## Current Features

* Reads log entries from a specified file.
* Parses log entries from the Apache Common Log Format.
* Counts the total number of unique IP addresses found in the log file.

## Setup and Usage

1.  **Compile:** Compile all `.java` files. If using an IDE, this is usually handled automatically. If compiling manually:
    ```bash
    javac LogEntry.java WebLogParser.java LogAnalyzer.java LogTester.java
    ```
2.  **Log File:** The program currently expects a log file named `short-test.log` located at the hardcoded absolute path within `LogTester.java`:
    `C:\Users\inouy\duke_coursera\WebLogStats\WebLogStats\lib\short-test.log`
    *Ensure this file exists at this location or modify the `filename` variable in `LogTester.java` to point to your log file.* The log file should be in the Apache Common Log Format.
3.  **Run:** Execute the `LogTester` class.
    ```bash
    java LogTester
    ```
    The program will output the results of the analysis performed in `LogTester.testLogAnalyzer()`.

## Future Enhancements (Potential)

* Add more analysis methods (e.g., visits per day, most common status codes).
* Make the log file path configurable (e.g., via command-line argument).
* Improve error handling for malformed log lines.
* Use more efficient data structures for larger logs where applicable.