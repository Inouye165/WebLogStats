import javax.swing.JFileChooser;
import java.io.File;
// Import needed for file extension filtering
import javax.swing.filechooser.FileNameExtensionFilter;
// Import needed for the graphical pop-up window
import javax.swing.JOptionPane;

/**
 * Tester class for LogAnalyzer focusing on counting unique IPs.
 * Uses JFileChooser to select the log file dynamically,
 * starting in a default directory and filtering for .log files.
 * Displays the final unique IP count in a graphical window using HTML.
 */
public class LogTester {

    public static void main(String[] args) {
        testLogAnalyzer();
    }

    public static void testLogAnalyzer() {
        // Create a LogAnalyzer object
        LogAnalyzer analyzer = new LogAnalyzer();

        // --- Use JFileChooser to select the log file ---
        JFileChooser fileChooser = new JFileChooser();

        // --- 1. Set the Default Directory ---
        // Define the path to your usual log file folder
        // Use forward slashes for better cross-platform compatibility in Java
        // *** ADJUST THIS PATH if necessary for your system ***
        String defaultPath = "C:/Users/inouy/duke_coursera/WebLogStats/WebLogStats/lib"; // Example path
        File defaultDirectory = new File(defaultPath);

        // Set the file chooser to start in that directory if it exists
        if (defaultDirectory.exists() && defaultDirectory.isDirectory()) {
            fileChooser.setCurrentDirectory(defaultDirectory);
            System.out.println("File chooser starting in: " + defaultPath);
        } else {
            System.out.println("Default directory (" + defaultPath + ") not found. Starting in user home directory.");
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        }

        // --- 2. Set the File Filter ---
        FileNameExtensionFilter logFilter = new FileNameExtensionFilter(
                "Log Files (*.log)", // Description shown in the filter dropdown
                "log"                // The file extension(s) to allow
        );
        fileChooser.setFileFilter(logFilter);
        // Optional: Control if "All Files" filter is shown
        // fileChooser.setAcceptAllFileFilterUsed(false); // Hide "All Files"

        System.out.println("Opening file chooser dialog (filtered for .log files)...");
        int result = fileChooser.showOpenDialog(null); // Show the dialog

        // --- Process the selected file (if one was chosen) ---
        if (result == JFileChooser.APPROVE_OPTION) {
            // User selected a file
            File selectedFile = fileChooser.getSelectedFile();
            String filename = selectedFile.getAbsolutePath(); // Get the full path
            String shortFilename = selectedFile.getName(); // Get just the file name for display

            System.out.println("Selected file: " + filename);

            // Read the selected log file
            // Consider adding a visual cue for long processing times if needed
            System.out.println("Reading and analyzing log file, please wait...");
            analyzer.readFile(filename);
            System.out.println("Finished reading and analyzing file.");

            // --- Test countUniqueIPs ---
            int uniqueIPCount = analyzer.countUniqueIPs();
            System.out.println("Analysis complete. Displaying results..."); // Console feedback

            // --- Display result in a graphical window using HTML ---
            String htmlMessage = String.format(
                "<html>" +
                "<body style='font-family: sans-serif; padding: 10px;'>" +
                "<h2 style='color: #00579B;'>Web Log Analysis Results</h2>" + // Blue heading
                "<hr>" +
                "<p>Analysis completed for file: <br><code style='font-size:0.9em; color:#555;'>%s</code></p>" + // Show filename
                "<p>Number of unique IP addresses found:</p>" +
                // Green, bold, larger font for the result
                "<p style='font-size: 1.8em; color: #008000; font-weight: bold; text-align: center;'>%d</p>" +
                "</body></html>",
                shortFilename, // Insert the short filename
                uniqueIPCount  // Insert the count
            );

            JOptionPane.showMessageDialog(
                null,                          // Parent component (null centers on screen)
                htmlMessage,                   // The HTML formatted message
                "Unique IP Count",             // Title of the window
                JOptionPane.INFORMATION_MESSAGE // Icon type
            );

            // Add other analysis calls here if needed...
            // You could potentially add more results to the htmlMessage string

        } else if (result == JFileChooser.CANCEL_OPTION) {
            System.out.println("File selection cancelled by user. Exiting.");
            // Optionally show a cancellation message window
            JOptionPane.showMessageDialog(null, "File selection cancelled.", "Cancelled", JOptionPane.WARNING_MESSAGE);
        } else {
            System.err.println("JFileChooser error or dialog closed unexpectedly. Exiting.");
            // Optionally show an error message window
            JOptionPane.showMessageDialog(null, "An error occurred with the file chooser.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}