/**
 * LogAnalyzer class for analyzing web server logs using WebLogParser
 *
 * @author Ron
 * @version 2.1 // Updated Version
 */

 import java.util.*;
 import java.io.IOException;
 import java.nio.file.Files;
 import java.nio.file.Paths;
 
 public class LogAnalyzer {
     private ArrayList<LogEntry> records;
 
     public LogAnalyzer() {
         records = new ArrayList<LogEntry>();
     }
 
     /**
      * Reads log entries from a file using WebLogParser.
      * @param filename The name of the log file to read.
      */
     public void readFile(String filename) {
         try {
             List<String> lines = Files.readAllLines(Paths.get(filename));
             for (String line : lines) {
                 // Use WebLogParser to create a LogEntry and add it to records
                 LogEntry entry = WebLogParser.parseEntry(line);
                 records.add(entry);
             }
         } catch (IOException e) {
             System.err.println("Error reading file: " + filename + " - " + e.getMessage());
         } catch (Exception e) {
              System.err.println("Error parsing line in file: " + filename + " - " + e.getMessage());
         }
     }
 
     /**
      * Counts the number of unique IP addresses in the log file.
      * This implementation follows the video's algorithm using an ArrayList.
      *
      * @return The count of unique IP addresses.
      */
     public int countUniqueIPs() {
         // Create an ArrayList to store the unique IP addresses found so far.
         ArrayList<String> uniqueIPs = new ArrayList<String>();
 
         // Iterate through each LogEntry in the records list.
         for (LogEntry le : records) {
             // Get the IP address from the current LogEntry.
             String ipAddr = le.getIpAddress();
 
             // Check if this IP address is already in our list of unique IPs.
             if (!uniqueIPs.contains(ipAddr)) {
                 // If it's not in the list, add it.
                 uniqueIPs.add(ipAddr);
             }
         }
         // The size of the uniqueIPs list is the total count of unique IPs.
         return uniqueIPs.size();
     }
 
 
     // --- Other methods like printAll, printAllHigherThanNum, etc. remain here ---
 
     /**
      * Prints all log entries stored.
      */
     public void printAll() {
         for (LogEntry le : records) {
             System.out.println(le);
         }
     }
 
     /**
      * Prints all log entries with status codes strictly greater than num.
      * @param num The threshold status code.
      */
     public void printAllHigherThanNum(int num) {
         System.out.println("Log entries with status code > " + num + ":");
         for (LogEntry le : records) {
             if (le.getStatusCode() > num) {
                 System.out.println(le);
             }
         }
     }
 
      /**
       * Returns a list of unique IP addresses that had status codes within the specified range (inclusive).
       * @param low The lower bound of the status code range.
       * @param high The upper bound of the status code range.
       * @return A List containing the unique IP addresses.
       */
     public ArrayList<String> uniqueIPsInRange(int low, int high) {
         // Using HashSet is more efficient for uniqueness checks
         HashSet<String> uniqueIPs = new HashSet<String>();
         for (LogEntry le : records) {
             int status = le.getStatusCode();
             if (status >= low && status <= high) {
                 uniqueIPs.add(le.getIpAddress());
             }
         }
         return new ArrayList<String>(uniqueIPs);
     }
 
      /**
       * Counts the number of unique IP addresses that had status codes within the specified range (inclusive).
       * @param low The lower bound of the status code range.
       * @param high The upper bound of the status code range.
       * @return The count of unique IP addresses in the range.
       */
     public int countUniqueIPsInRange(int low, int high) {
          return uniqueIPsInRange(low, high).size();
      }
 }