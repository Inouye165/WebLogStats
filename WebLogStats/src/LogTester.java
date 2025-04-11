// FILE: LogTester.java
// Reverted to the version WITHOUT predefined tests to match the updated LogAnalysisWindow constructor

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.io.File;
import java.io.IOException; // Import IOException

public class LogTester {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            runAnalysisSetup();
        });
    }

    public static void runAnalysisSetup() {
        // No predefined tests section anymore

        LogAnalyzer analyzer = new LogAnalyzer(); // Single analyzer

        // --- 1. Select Log File ---
        File selectedFile = selectLogFile();
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(null, "No file selected. Exiting.", "Cancelled", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String filename = selectedFile.getAbsolutePath();
        String shortFilename = selectedFile.getName();
        System.out.println("Selected file: " + filename);

        // --- 2. Read and Analyze Selected File ---
        try {
            System.out.println("Reading and analyzing selected log file, please wait...");
            analyzer.readFile(filename);
            System.out.println("Finished reading and analyzing selected file.");
        } catch (IOException e) {
             System.err.println("ERROR: Could not read selected file: " + filename);
             JOptionPane.showMessageDialog(null, "Error for some reson" ); // Error dialog
             return;
        } catch (Exception e) {
             System.err.println("ERROR during file processing: " + e.getMessage());
             JOptionPane.showMessageDialog(null, "Error for some reason #2" ); // Error dialog
             e.printStackTrace();
             return;
        }

        // --- 3. Capture Range Results (from selected file) ---
        int count200s = analyzer.countUniqueIPsInRange(200, 299);
        int count300s = analyzer.countUniqueIPsInRange(300, 399);
        String rangeResults = String.format(
            "Unique IPs with status 200-299: %d\nUnique IPs with status 300-399: %d",
            count200s, count300s
        );

        // --- 4. Create and Show the Main Analysis Window ---
        // Call constructor with 3 arguments
        new LogAnalysisWindow(
                analyzer,
                shortFilename,
                rangeResults); // Only 3 arguments

        System.out.println("\n--- Analysis window launched. ---");
    }

    // selectLogFile method remains the same
// Replace the line: private static File selectLogFile() { /* ... unchanged ... */ }
// With this complete method:
private static File selectLogFile() {
    JFileChooser fileChooser = new JFileChooser();

    // OPTIONAL: Set a default starting directory if desired
    String defaultPath = "C:\\Users\\inouy\\duke_coursera\\WebLogStats\\WebLogStats\\lib"; // Adjust this path
    File defaultDirectory = new File(defaultPath);
    if (defaultDirectory.exists() && defaultDirectory.isDirectory()) {
    fileChooser.setCurrentDirectory(defaultDirectory);
    }

    // Set a filter for .log files
    FileNameExtensionFilter logFilter = new FileNameExtensionFilter("Log Files (*.log)", "log");
    fileChooser.setFileFilter(logFilter);
    fileChooser.setAcceptAllFileFilterUsed(true); // Allows user to select "All Files"

    System.out.println("Opening file chooser dialog...");
    int result = fileChooser.showOpenDialog(null); // Parent component is null

    if (result == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        // System.out.println("File selected: " + selectedFile.getAbsolutePath()); // LogTester already prints this
        return selectedFile; // Return the chosen file
    } else {
        System.out.println("File selection cancelled by user or failed.");
        return null; // Return null if no file was chosen
    }
}
} // End of LogTester class