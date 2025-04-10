import javax.swing.JFileChooser;
import java.io.File;
// Import needed for file extension filtering
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Tester class for LogAnalyzer focusing on counting unique IPs.
 * Uses JFileChooser to select the log file dynamically,
 * starting in a default directory and filtering for .txt files.
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
        String defaultPath = "C:/Users/inouy/duke_coursera/WebLogStats/WebLogStats/lib";
        File defaultDirectory = new File(defaultPath);

        // Set the file chooser to start in that directory if it exists
        if (defaultDirectory.exists() && defaultDirectory.isDirectory()) {
            fileChooser.setCurrentDirectory(defaultDirectory);
            System.out.println("File chooser starting in: " + defaultPath);
        } else {
            // Optional: Fallback if the specific directory isn't found
            System.out.println("Default directory (" + defaultPath + ") not found. Starting in user home directory.");
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            // Or fallback to the working directory: fileChooser.setCurrentDirectory(new File("."));
        }

        // --- 2. Set the File Filter ---
        // Create a filter that describes and accepts only .txt files
        FileNameExtensionFilter textFilter = new FileNameExtensionFilter(
                "Log Files (*.log)", // Description shown in the filter dropdown
                "log"                 // The file extension(s) to allow (without the dot)
        );
        // Apply the filter to the file chooser
        fileChooser.setFileFilter(textFilter);
        // Optional: You can control if the "All Files" filter is shown. Default is true.
        // fileChooser.setAcceptAllFileFilterUsed(true); // Keep "All Files" option available
        // fileChooser.setAcceptAllFileFilterUsed(false); // Hide "All Files" option

        System.out.println("Opening file chooser dialog (filtered for .txt files)...");
        int result = fileChooser.showOpenDialog(null); // Show the dialog

        // --- Process the selected file (if one was chosen) ---
        if (result == JFileChooser.APPROVE_OPTION) {
            // User selected a file
            File selectedFile = fileChooser.getSelectedFile();
            String filename = selectedFile.getAbsolutePath(); // Get the full path

            System.out.println("Selected file: " + filename);

            // Read the selected log file
            analyzer.readFile(filename);
            System.out.println("Finished reading file attempt.");

            // --- Test countUniqueIPs ---
            int uniqueIPCount = analyzer.countUniqueIPs();
            System.out.println("\n--- Unique IP Count Analysis ---");
            System.out.println("Number of unique IP addresses found: " + uniqueIPCount);
            System.out.println("--- End of Analysis ---");

            // Add other analysis calls here...

        } else if (result == JFileChooser.CANCEL_OPTION) {
            // User cancelled
            System.out.println("File selection cancelled by user. Exiting.");
        } else {
            // Error
            System.err.println("JFileChooser error. Exiting.");
        }
    }
}