package config;

import core.AlgorithmRunner;
import org.yaml.snakeyaml.Yaml;
import utils.CSVWriter;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class ConfigReader {
    public static void main(String[] args) {
        Yaml yaml = new Yaml();

        try (InputStream inputStream = new FileInputStream("config.yaml")) {
            // Read the YAML Configuration
            Map<String, Object> config = yaml.load(inputStream);
            String experimentName = (String) config.get("experiment_name");
            String datasetPath = (String) config.get("dataset_path");
            Double minSupport = (Double) config.get("min_support");

            @SuppressWarnings("unchecked")
            List<String> algorithms = (List<String>) config.get("algorithms");

            System.out.println("=====================================");
            System.out.println("BENCHMARK PLATFORM - STARTING RUN");
            System.out.println("=====================================");

            // Call Engine and Exporter
            AlgorithmRunner runner = new AlgorithmRunner();
            CSVWriter writer = new CSVWriter();

            // Path and names
            String rawOutputPath = "output/raw_itemsets.txt";

            // Main execution loop
            for (String algo : algorithms) {
                // Run the math and capture the time/memory
                Map<String, Object> metrics = runner.runAlgorithm(algo, datasetPath, rawOutputPath, minSupport);

                // If successful, save it to the CSV
                if (metrics != null) {
                    writer.writeResult(experimentName, datasetPath, metrics);
                }
            }

            System.out.println("\n=====================================");
            System.out.println("BENCHMARK COMPLETE. Check output/results.csv");
            System.out.println("=====================================");

        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: Failed to execute platform.");
            System.err.println("Details: " + e.getMessage());
        }
    }
}