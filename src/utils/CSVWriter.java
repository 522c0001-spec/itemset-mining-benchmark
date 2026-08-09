package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class CSVWriter {

    private final String defaultOutputPath = "output/results.csv";

    public void writeResult(String experimentName, String dataset, Map<String, Object> metrics) {
        if (metrics == null || metrics.isEmpty()) {
            System.err.println("Warning: No metrics to write. Skipping CSV export.");
            return;
        }

        File file = new File(defaultOutputPath);
        boolean isNewFile = !file.exists();

        try (FileWriter fw = new FileWriter(file, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            if (isNewFile) {
                out.println("Experiment_Name,Dataset,Algorithm,MinSupport,Execution_Time_ms,Memory_Used_MB,Itemset_Count");
            }

            String algorithm = (String) metrics.get("Algorithm");
            if (algorithm == null) algorithm = (String) metrics.get("algorithm");

            long executionTime = (Long) metrics.get("executionTime");
            double memoryMB = (Double) metrics.get("memoryMB");

            Double minSupport = (Double) metrics.get("MinSupport");
            if (minSupport == null) minSupport = 0.0;

            long itemsetCount = (Long) metrics.getOrDefault("itemsetCount", 0L);

            out.printf("%s,%s,%s,%.2f,%d,%.2f,%d\n",
                    experimentName,
                    dataset,
                    algorithm,
                    minSupport,
                    executionTime,
                    memoryMB,
                    itemsetCount);

            System.out.println("    [+] Results successfully saved to " + defaultOutputPath);

        } catch (IOException e) {
            System.err.println("CRITICAL: Failed to write to CSV file.");
            System.err.println(e.getMessage());
        }
    }
}