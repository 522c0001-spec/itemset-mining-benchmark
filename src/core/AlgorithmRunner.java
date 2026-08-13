package core;

import core.algorithms.IMiningAlgorithm;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import com.sun.management.ThreadMXBean;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class AlgorithmRunner {

    /**
     * Executes any data mining algorithm dynamically using Java Reflection and Plugin Loading.
     *
     * @param className  The fully qualified class name (e.g., "core.algorithms.AprioriWrapper" or "StrangerTopK")
     * @param classDir   The absolute directory path to the external .class file (null for internal algorithms)
     * @param parameters The dynamic map of parameters for the algorithm
     * @param inputPath  Path to the dataset
     * @param outputPath Path to save the itemsets
     * @return Metrics map for the CSV Writer
     */
    public Map<String, Object> runAlgorithm(String className, String classDir, Map<String, Object> parameters, String inputPath, String outputPath) {

        String shortName = className;
        if (className.contains(".")) {
            shortName = className.substring(className.lastIndexOf('.') + 1);
        }

        System.out.println("\n* Initializing: " + shortName + " (Parameters: " + parameters + ")");
        System.gc();

        ThreadMXBean threadBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        long threadId = Thread.currentThread().threadId();
        long startMemory = threadBean.getThreadAllocatedBytes(threadId);

        long startTime = System.currentTimeMillis();
        long itemsetCount = 0;

        try {
            Class<?> clazz;

            if (classDir != null && !classDir.trim().isEmpty()) {
                // Load from an external folder provided by the user
                File file = new File(classDir);
                URL url = file.toURI().toURL();
                URLClassLoader loader = new URLClassLoader(
                        new URL[]{url},
                        this.getClass().getClassLoader() // Share the IMiningAlgorithm interface
                );
                clazz = Class.forName(className, true, loader);
            } else {
                // Load standard internal algorithms from the jar
                clazz = Class.forName(className);
            }

            IMiningAlgorithm algorithm = (IMiningAlgorithm) clazz.getDeclaredConstructor().newInstance();
            itemsetCount = algorithm.run(parameters, inputPath, outputPath);

        } catch (ClassNotFoundException e) {
            System.err.println("CRITICAL: Could not find algorithm class: " + className);
            if (classDir != null) {
                System.err.println("Make sure the .class file is inside the directory: " + classDir);
            }
            return null;
        } catch (Exception e) {
            System.err.println("CRITICAL: Algorithm execution failed.");
            e.printStackTrace();
            return null;
        }

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        long endMemory = threadBean.getThreadAllocatedBytes(threadId);
        long memoryUsedBytes = endMemory - startMemory;
        double memoryUsedMB = memoryUsedBytes / (1024.0 * 1024.0);

        System.out.println("    [-] Execution Time: " + executionTime + " ms");
        System.out.printf("    [-] Memory Consumed: %.2f MB\n", memoryUsedMB);
        System.out.println("    [-] Itemsets Found: " + itemsetCount);

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("algorithm", shortName);
        metrics.put("executionTime", executionTime);
        metrics.put("memoryMB", memoryUsedMB);
        metrics.put("itemsetCount", itemsetCount);

        if (parameters.containsKey("minSupport")) {
            metrics.put("MinSupport", Double.parseDouble(parameters.get("minSupport").toString()));
        } else {
            metrics.put("MinSupport", 0.0);
        }

        return metrics;
    }
}