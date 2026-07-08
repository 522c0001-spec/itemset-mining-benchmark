package core;
import ca.pfv.spmf.algorithms.frequentpatterns.apriori_simple.AlgoApriori;
import ca.pfv.spmf.algorithms.frequentpatterns.eclat.AlgoEclat;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPGrowth;
import ca.pfv.spmf.input.transaction_database_list_integers.TransactionDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AlgorithmRunner {

    /**
     * Executes a specific data mining algorithm and records its performance.
     * Returns a Map containing the execution time and memory used.
     */
    public Map<String, Object> runAlgorithm(String algorithmName, String inputPath, String outputPath, double minSupport) {
        System.out.println("\n* Initializing: " + algorithmName + " (Min Support: " + minSupport + ")");

        // Force Java to dump unneeded memory before testing
        System.gc();

        Runtime runtime = Runtime.getRuntime();
        long startMemory = runtime.totalMemory() - runtime.freeMemory();

        // Start the Stopwatch
        long startTime = System.currentTimeMillis();

        try {
            // The Execution Switchboard
            if (algorithmName.equalsIgnoreCase("Apriori")) {
                AlgoApriori apriori = new AlgoApriori();
                // SPMF Apriori takes minSupport as a percentage (e.g., 0.4)
                apriori.runAlgorithm(minSupport, inputPath, outputPath);
            }
            else if (algorithmName.equalsIgnoreCase("FPGrowth")) {
                AlgoFPGrowth fpGrowth = new AlgoFPGrowth();
                fpGrowth.runAlgorithm(inputPath, outputPath, minSupport);
            }
            else if (algorithmName.equalsIgnoreCase("ECLAT")) {
                // Convert raw text file into a Database object first
                TransactionDatabase database = new TransactionDatabase();
                database.loadFile(inputPath);

                // Run Éclat with the 4 required arguments (Output, Database, MinSupport, SaveTIDs)
                AlgoEclat eclat = new AlgoEclat();
                eclat.runAlgorithm(outputPath, database, minSupport, false);
            }
            else {
                System.out.println("Error: Algorithm '" + algorithmName + "' is not supported yet.");
                return null;
            }

            // Stop the Stopwatch
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            // Calculate the Memory Footprint
            long endMemory = runtime.totalMemory() - runtime.freeMemory();
            long memoryUsedBytes = endMemory - startMemory;

            // Convert bytes to Megabytes for readability.
            // If negative (due to background GC), default to 0.
            double memoryUsedMB = Math.max(0, memoryUsedBytes / (1024.0 * 1024.0));

            System.out.println("    [-] Execution Time: " + executionTime + " ms");
            System.out.printf("    [-] Memory Consumed: %.2f MB\n", memoryUsedMB);

            // Package the results to send to CSV Writer
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("algorithm", algorithmName);
            metrics.put("executionTime", executionTime);
            metrics.put("memoryMB", memoryUsedMB);

            return metrics;

        } catch (IOException e) {
            System.err.println("CRITICAL: Failed to process dataset file.");
            System.err.println(e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("CRITICAL: Algorithm execution failed.");
            System.err.println(e.getMessage());
            return null;
        }
    }
}