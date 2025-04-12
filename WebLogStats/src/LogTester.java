import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap; // Ensure HashMap is imported

public class LogTester {

    public static void main(String[] args) {
        // Ensure GUI operations are on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            runAnalysisSetup();
        });
    }

    public static void runAnalysisSetup() {
        // --- 0. Ask User for Analysis Type ---
        // Add the new option
        String[] options = {"Finding Unique IP Addresses", "Counting Website Visits", "Overall Log Statistics"};
        int choice = JOptionPane.showOptionDialog(
                null, // Parent component
                "Which analysis would you like to perform?", // Message
                "Select Analysis Type", // Title
                JOptionPane.DEFAULT_OPTION, // Use default option type for 3+ options
                JOptionPane.QUESTION_MESSAGE, // Message type
                null, // Icon
                options, // Options array
                options[0] // Default option
        );

        // Handle if the user closed the dialog (returns -1 or choice index)
        if (choice == JOptionPane.CLOSED_OPTION) {
            System.out.println("Analysis selection cancelled. Exiting.");
            return;
        }

        // --- 1. Select Log File (Same as before) ---
        File selectedFile = selectLogFile();
        if (selectedFile == null) {
            // selectLogFile prints cancellation message, show dialog just in case
             JOptionPane.showMessageDialog(null, "No file selected. Exiting.", "Cancelled", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String filename = selectedFile.getAbsolutePath();
        String shortFilename = selectedFile.getName();
        System.out.println("Selected file: " + filename);

        // --- 2. Read and Analyze Selected File (Common setup) ---
        LogAnalyzer analyzer = new LogAnalyzer();
        try {
            System.out.println("Reading and analyzing selected log file, please wait...");
            analyzer.readFile(filename); // Reads all records into the analyzer
            System.out.println("Finished reading and analyzing selected file.");
        } catch (IOException e) {
            System.err.println("ERROR: Could not read selected file: " + filename + " - " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error reading log file:\n" + e.getMessage(), "File Read Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit if file cannot be read
        } catch (Exception e) {
            // Catch potential parsing errors or other issues during readFile
            System.err.println("ERROR during file processing: " + e.getMessage());
             JOptionPane.showMessageDialog(null, "An unexpected error occurred during processing:\n" + e.getMessage(), "Processing Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Print stack trace for debugging
            return; // Exit on unexpected errors
        }

        // --- 3. Perform Chosen Analysis and Launch Window ---
        switch (choice) {
            case 0: // User chose "Finding Unique IP Addresses" (Index 0)
                System.out.println("Performing Unique IP Address Analysis...");
                // Calculate initial range results needed for the original window
                int count200s = analyzer.countUniqueIPsInRange(200, 299);
                int count300s = analyzer.countUniqueIPsInRange(300, 399);
                String rangeResults = String.format(
                        "Unique IPs with status 200-299: %d\nUnique IPs with status 300-399: %d",
                        count200s, count300s
                );
                // Launch the original LogAnalysisWindow (ensure it exists and compiles)
                 new LogAnalysisWindow(analyzer, shortFilename, rangeResults);
                System.out.println("\n--- Unique IP Analysis window launched. ---");
                break;

            case 1: // User chose "Counting Website Visits" (Index 1)
                System.out.println("Performing Website Visit Count Analysis...");
                // Call the method to get the counts
                HashMap<String, Integer> ipCounts = analyzer.countVisitsPerIP();
                 // Launch the VisitCountWindow (ensure it exists and compiles)
                new VisitCountWindow(shortFilename, ipCounts);
                System.out.println("\n--- Visit Count Analysis window launched. ---");
                break;

            case 2: // User chose "Overall Log Statistics" (Index 2)
                 System.out.println("Performing Overall Log Statistics Analysis...");
                 // Launch the new SummaryStatsWindow, passing the analyzer itself
                 new SummaryStatsWindow(analyzer, shortFilename);
                 System.out.println("\n--- Summary Stats window launched. ---");
                 break;

            default:
                // This case should ideally not be reached if JOptionPane is used correctly
                System.out.println("Invalid choice selected (" + choice + ").");
                break;
        }
    }

    // selectLogFile method remains the same
    private static File selectLogFile() {
        JFileChooser fileChooser = new JFileChooser();

        // OPTIONAL: Set a default starting directory if desired
        // Make sure this path is valid on your system or remove/comment out this section
        String defaultPath = "C:\\Users\\inouy\\duke_coursera\\WebLogStats\\WebLogStats\\lib"; // ADJUST THIS PATH
        File defaultDirectory = new File(defaultPath);
        if (defaultDirectory.exists() && defaultDirectory.isDirectory()) {
           fileChooser.setCurrentDirectory(defaultDirectory);
        } else {
            System.out.println("Warning: Default directory not found: " + defaultPath + ". Using default chooser location.");
            // Optionally set to user's home directory or let JFileChooser decide
            // fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        }


        // Set a filter for .log files
        FileNameExtensionFilter logFilter = new FileNameExtensionFilter("Log Files (*.log)", "log");
        fileChooser.setFileFilter(logFilter);
        fileChooser.setAcceptAllFileFilterUsed(true); // Allows user to select "All Files"

        System.out.println("Opening file chooser dialog...");
        int result = fileChooser.showOpenDialog(null); // Parent component is null

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile; // Return the chosen file
        } else {
            System.out.println("File selection cancelled by user or failed.");
            return null; // Return null if no file was chosen
        }
    }
} // End of LogTester class