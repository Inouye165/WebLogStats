/**
 * Tester class for LogAnalyzer focusing on counting unique IPs.
 */
public class LogTester {

    public static void main(String[] args) {
        testLogAnalyzer();
    }

    public static void testLogAnalyzer() {
        // Create a LogAnalyzer object
        LogAnalyzer analyzer = new LogAnalyzer();

        // Specify the log file to read
        // Assumes "short-list_log.txt" is in the project's root directory
        // or a "lib" folder if you adjust the path like "lib/short-list_log.txt"
        String filename = "C:\\Users\\inouy\\duke_coursera\\WebLogStats\\WebLogStats\\lib\\short-test.log";
        // Read the log file
        analyzer.readFile(filename);
        System.out.println("Finished reading file.");

        // --- Test countUniqueIPs ---
        int uniqueIPCount = analyzer.countUniqueIPs();
        System.out.println("\n--- Unique IP Count Test ---");
        System.out.println("Number of unique IP addresses: " + uniqueIPCount);
        System.out.println("Expected count for short-list_log.txt: 4"); // Based on your analysis
        System.out.println("--- End of Test ---");

        // You can add calls to other analysis methods here if needed
        // For example:
        // System.out.println("\nEntries with status code > 300:");
        // analyzer.printAllHigherThanNum(300);
        //
        // int uniqueInRange = analyzer.countUniqueIPsInRange(200, 299);
        // System.out.println("\nUnique IPs with status 200-299: " + uniqueInRange);
    }
}