package core;

import ca.pfv.spmf.algorithms.frequentpatterns.apriori_simple.AlgoApriori;
import ca.pfv.spmf.algorithms.frequentpatterns.eclat.AlgoEclat;
import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPGrowth;
import ca.pfv.spmf.input.transaction_database_list_integers.TransactionDatabase;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

// Import the Sun-specific bean for thread-level allocation tracking
import com.sun.management.ThreadMXBean;

public class AlgorithmRunner {

    /**
     * Executes a specific data mining algorithm and records its performance.
     * Returns a Map containing the execution time, memory used, and itemsets mined.
     */
    public Map<String, Object> runAlgorithm(String algorithmName, String inputPath, String outputPath, double minSupport) {
        System.out.println("\n* Initializing: " + algorithmName + " (Min Support: " + minSupport + ")");

        System.gc(); // Optional safeguard, though less critical now with ThreadMXBean

        // 1. Setup ThreadMXBean to completely isolate memory tracking from the GC
        ThreadMXBean threadBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        long threadId = Thread.currentThread().getId();
        long startMemory = threadBean.getThreadAllocatedBytes(threadId);

        // Start the Stopwatch
        long startTime = System.currentTimeMillis();

        try {
            // The Execution Switchboard
            if (algorithmName.equalsIgnoreCase("Apriori")) {
                AlgoApriori apriori = new AlgoApriori();
                apriori.runAlgorithm(minSupport, inputPath, outputPath);
            }
            else if (algorithmName.equalsIgnoreCase("FPGrowth")) {
                AlgoFPGrowth fpGrowth = new AlgoFPGrowth();
                fpGrowth.runAlgorithm(inputPath, outputPath, minSupport);
            }
            else if (algorithmName.equalsIgnoreCase("ECLAT")) {
                TransactionDatabase database = new TransactionDatabase();
                database.loadFile(inputPath);

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

            // 2. Calculate ThreadMXBean Memory
            long endMemory = threadBean.getThreadAllocatedBytes(threadId);
            long memoryUsedBytes = endMemory - startMemory;

            // We no longer need Math.max() because allocated bytes strictly increases
            double memoryUsedMB = memoryUsedBytes / (1024.0 * 1024.0);

            // 3. Count the number of mined itemsets
            long itemsetCount = 0;
            try (BufferedReader reader = new BufferedReader(new FileReader(outputPath))) {
                while (reader.readLine() != null) {
                    itemsetCount++;
                }
            }

            System.out.println("    [-] Execution Time: " + executionTime + " ms");
            System.out.printf("    [-] Memory Consumed: %.2f MB\n", memoryUsedMB);
            System.out.println("    [-] Itemsets Found: " + itemsetCount);

            // Package the results to send to CSV Writer
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("algorithm", algorithmName);
            metrics.put("executionTime", executionTime);
            metrics.put("memoryMB", memoryUsedMB);
            metrics.put("itemsetCount", itemsetCount);

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