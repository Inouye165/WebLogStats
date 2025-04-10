import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
// No other GUI imports needed here anymore

public class LogTester {

    public static void main(String[] args) {
        // Use SwingUtilities.invokeLater to ensure GUI creation happens on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            runAnalysisSetup();
        });
    }

    public static void runAnalysisSetup() {
        LogAnalyzer analyzer = new LogAnalyzer();

        // --- 1. Select Log File ---
        File selectedFile = selectLogFile();
        if (selectedFile == null) {
            // Show message using JOptionPane as it's before main window exists
            JOptionPane.showMessageDialog(null, "No file selected. Exiting.", "Cancelled", JOptionPane.WARNING_MESSAGE);
            return; // Exit if no file was chosen
        }
        String filename = selectedFile.getAbsolutePath();
        String shortFilename = selectedFile.getName();
        System.out.println("Selected file: " + filename);

        // --- 2. Read and Analyze File (populates analyzer) ---
        System.out.println("Reading and analyzing log file, please wait...");
        analyzer.readFile(filename); // Reads the file and finds min/max dates
        System.out.println("Finished reading and analyzing file.");

        // --- 3. Create and Show the Main Analysis Window ---
        // Pass the analyzer instance (with loaded data) and filename to the window
        LogAnalysisWindow analysisWindow = new LogAnalysisWindow(analyzer, shortFilename);
        analysisWindow.setVisible(true); // Show the main window

        // --- Console outputs can still happen here if needed ---
        // These run *after* the file is read but *before* user interacts with date picker

        // Example: Print Status Code > Num (Console Output)
        analyzer.printAllHigherThanNum(400);

        // Example: Count Unique IPs In Range (Console Output)
        int count200s = analyzer.countUniqueIPsInRange(200, 299);
        System.out.println("\nUnique IPs with status 200-299 (Console): " + count200s);
        int count300s = analyzer.countUniqueIPsInRange(300, 399);
        System.out.println("Unique IPs with status 300-399 (Console): " + count300s);

        System.out.println("\n--- Analysis window launched. Select date in window to see daily results. ---");
    }

    /** Helper method to select the log file using JFileChooser */
    private static File selectLogFile() {
        JFileChooser fileChooser = new JFileChooser();

        // --- Set Default Directory ---
        // *** VERIFY THIS PATH IS CORRECT FOR YOUR SYSTEM ***
        String defaultPath = "C:/Users/inouy/duke_coursera/WebLogStats/WebLogStats/lib"; // Your specific lib path

        File defaultDirectory = new File(defaultPath);

        if (defaultDirectory.exists() && defaultDirectory.isDirectory()) {
            fileChooser.setCurrentDirectory(defaultDirectory);
            System.out.println("File chooser starting in: " + defaultPath);
        } else {
            // Fallback if the specific directory isn't found
            System.out.println("Specified default directory (" + defaultPath + ") not found or is not a directory.");
            System.out.println("Starting in user home directory instead.");
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        }
        // --- End Set Default Directory ---


        // --- Set File Filter ---
        FileNameExtensionFilter logFilter = new FileNameExtensionFilter("Log Files (*.log)", "log");
        fileChooser.setFileFilter(logFilter);
        fileChooser.setAcceptAllFileFilterUsed(true); // Allow "All Files"

        System.out.println("Opening file chooser dialog...");
        int result = fileChooser.showOpenDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else {
            System.out.println("File selection cancelled or failed.");
            return null; // Indicate cancellation or error
        }
    }

} // End of LogTester class