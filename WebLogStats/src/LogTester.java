// FILE: LogTester.java

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException; // Import IOException
import java.util.ArrayList; // Needed for formatting test results

public class LogTester {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            runAnalysisSetup();
        });
    }

    public static void runAnalysisSetup() {
        String predefinedTestResults = "(Predefined tests did not run or failed)"; // Default message

        // --- 0. Run Predefined Assignment Tests ---
        try { // try block for predefined test file read
            LogAnalyzer assignmentTester = new LogAnalyzer();

            // *** CHECK / EDIT THIS PATH OR MOVE THE FILE ***
            String predefinedTestFile = "C:\\Users\\inouy\\duke_coursera\\WebLogStats\\WebLogStats\\lib\\weblog-short_log.log"; // Assumes file is in project root

            System.out.println("Running predefined tests on: " + predefinedTestFile);

            assignmentTester.readFile(predefinedTestFile); // Call the potentially throwing method

            // These lines only run if readFile succeeds
            ArrayList<String> ipsSep14 = assignmentTester.uniqueIPVisitsOnDay("Sep 14");
            ArrayList<String> ipsSep30 = assignmentTester.uniqueIPVisitsOnDay("Sep 30");

            // Format the results into a string
            predefinedTestResults = String.format( // Update the results string on success
                "Results for %s:\n" +
                "uniqueIPVisitsOnDay(\"Sep 14\"): Count = %d (Expected: 2)\n" +
                "uniqueIPVisitsOnDay(\"Sep 30\"): Count = %d (Expected: 3)",
                predefinedTestFile,
                ipsSep14.size(),
                ipsSep30.size()
            );
            System.out.println("Predefined test results calculated.");

        } catch (IOException e) { // catch block for predefined test file read
            System.err.println("FATAL ERROR during predefined tests: Could not read file '" + "weblog-short_log" + "'.");
            JOptionPane.showMessageDialog(null,
                "Error reading required test file 'weblog-short_log':\n" + e.getMessage() +
                "\nPlease ensure the file exists (e.g., in project root) or fix path in LogTester.java.\nApplication will now exit.",
                "Fatal Error", JOptionPane.ERROR_MESSAGE);
            return; // STOP execution
        } catch (Exception e) { // Catch other unexpected errors
             System.err.println("FATAL ERROR during predefined tests: " + e.getMessage());
             JOptionPane.showMessageDialog(null,"An unexpected error occurred during predefined tests:\n" + e.getMessage() + "\nApplication will now exit.", "Fatal Error", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
             return; // STOP execution
        }
        // --- End Predefined Tests ---


        // --- Proceed with interactive analysis setup ONLY IF predefined tests passed ---
        LogAnalyzer interactiveAnalyzer = new LogAnalyzer(); // Analyzer for user's chosen file

        // --- 1. Select Log File (for interactive analysis) ---
        File selectedFile = selectLogFile(); // This HELPER method contains the JFileChooser
        if (selectedFile == null) {
            return; // Exit if no file was chosen
        }
        String filename = selectedFile.getAbsolutePath();
        String shortFilename = selectedFile.getName();
        System.out.println("Selected file for interactive analysis: " + filename);

        // --- 2. Read and Analyze User's File ---
        try { // try block for selected file read
            System.out.println("Reading and analyzing selected log file, please wait...");
            interactiveAnalyzer.readFile(filename); // Call the potentially throwing method
            System.out.println("Finished reading and analyzing selected file.");
        } catch (IOException e) { // catch block for selected file read
             System.err.println("ERROR: Could not read selected file: " + filename);
             JOptionPane.showMessageDialog(null, "Error reading selected file:\n" + filename + "\n" + e.getMessage() + "\nPlease check the file and try again.", "File Read Error", JOptionPane.ERROR_MESSAGE);
             return; // Stop
        } catch (Exception e) { // Catch other unexpected errors
             System.err.println("ERROR during interactive file processing: " + e.getMessage());
             JOptionPane.showMessageDialog(null, "An unexpected error occurred processing the selected file:\n" + e.getMessage(), "Processing Error", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
             return; // Stop
        }

        // --- 3. Capture Range Results (from user's file) ---
        int count200s = interactiveAnalyzer.countUniqueIPsInRange(200, 299);
        int count300s = interactiveAnalyzer.countUniqueIPsInRange(300, 399);
        String rangeResults = String.format(
            "Unique IPs with status 200-299: %d\nUnique IPs with status 300-399: %d",
            count200s, count300s
        );

        // --- 4. Create and Show the Main Analysis Window ---
        // Pass analyzer, filename, range results, AND predefined results
        new LogAnalysisWindow(
                interactiveAnalyzer,
                shortFilename,
                rangeResults,
                predefinedTestResults); // <<<< Has 4 arguments here

        System.out.println("\n--- Analysis window launched. ---");
    }

    /** Helper method to select the log file using JFileChooser */
    private static File selectLogFile() {
        JFileChooser fileChooser = new JFileChooser();
        // --- Set Default Directory ---
        String defaultPath = "C:/Users/inouy/duke_coursera/WebLogStats/WebLogStats/lib"; // Your specific lib path
        File defaultDirectory = new File(defaultPath);
        if (defaultDirectory.exists() && defaultDirectory.isDirectory()) { fileChooser.setCurrentDirectory(defaultDirectory); System.out.println("File chooser starting in: " + defaultPath); }
        else { System.out.println("Specified default directory (" + defaultPath + ") not found or is not a directory."); System.out.println("Starting in user home directory instead."); fileChooser.setCurrentDirectory(new File(System.getProperty("user.home"))); }
        // --- Set File Filter ---
        FileNameExtensionFilter logFilter = new FileNameExtensionFilter("Log Files (*.log)", "log"); fileChooser.setFileFilter(logFilter); fileChooser.setAcceptAllFileFilterUsed(true);
        System.out.println("Opening file chooser dialog (for interactive analysis)..."); int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) { return fileChooser.getSelectedFile(); }
        else { System.out.println("File selection cancelled or failed."); return null; }
    }

} // End of LogTester class