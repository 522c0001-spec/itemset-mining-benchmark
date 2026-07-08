package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class CSVWriter {

    private final String defaultOutputPath = "output/results.csv";

    /**
     * Appends a single benchmark result as a new row in the CSV file.
     */
    public void writeResult(String experimentName, String dataset, Map<String, Object> metrics) {
        if (metrics == null || metrics.isEmpty()) {
            System.err.println("Warning: No metrics to write. Skipping CSV export.");
            return;
        }

        File file = new File(defaultOutputPath);
        boolean isNewFile = !file.exists();

        // Use FileWriter with 'true' to enable append mode
        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            // If the file is brand new, write the column headers first
            if (isNewFile) {
                out.println("Experiment_Name,Dataset,Algorithm,Min_Support,Execution_Time_ms,Memory_Used_MB");
            }

            // Extract the data from the metrics map
            String algorithm = (String) metrics.get("algorithm");
            long executionTime = (Long) metrics.get("executionTime");
            double memoryMB = (Double) metrics.get("memoryMB");

            out.printf("%s,%s,%s,%.2f,%d,%.2f\n",
                    experimentName,
                    dataset,
                    algorithm,
                    0.0, // We will update this to dynamic minSupport in the main loop
                    executionTime,
                    memoryMB);

            System.out.println("    [+] Results successfully saved to " + defaultOutputPath);

        } catch (IOException e) {
            System.err.println("CRITICAL: Failed to write to CSV file.");
            System.err.println(e.getMessage());
        }
    }
}