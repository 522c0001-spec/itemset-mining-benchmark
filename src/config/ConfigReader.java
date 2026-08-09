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
            Map<String, Object> config = yaml.load(inputStream);
            String experimentName = (String) config.get("experiment_name");
            String datasetPath = (String) config.get("dataset_path");
            
            // FIX 1: Read a list of supports instead of a single Double
            @SuppressWarnings("unchecked")
            List<Double> minSupports = (List<Double>) config.get("min_supports");

            @SuppressWarnings("unchecked")
            List<String> algorithms = (List<String>) config.get("algorithms");

            System.out.println("=====================================");
            System.out.println("BENCHMARK PLATFORM - STARTING BATCH RUN");
            System.out.println("=====================================");

            AlgorithmRunner runner = new AlgorithmRunner();
            CSVWriter writer = new CSVWriter();
            String rawOutputPath = "output/raw_itemsets.txt";

            // FIX 2: Nested loop to run all algorithms across all supports
            for (Double minSupport : minSupports) {
                for (String algo : algorithms) {
                    System.out.println("Testing " + algo + " at Support: " + minSupport);
                    
                    Map<String, Object> metrics = runner.runAlgorithm(algo, datasetPath, rawOutputPath, minSupport);

                    if (metrics != null) {
                        // FIX 3: Inject MinSupport and Algorithm so CSVWriter catches it
                        metrics.put("Algorithm", algo);
                        metrics.put("MinSupport", minSupport);
                        
                        writer.writeResult(experimentName, datasetPath, metrics);
                    }
                }
            }

            System.out.println("\n=====================================");
            System.out.println("BENCHMARK COMPLETE. Check output/results.csv");
            System.out.println("=====================================");

        } catch (Exception e) {
            System.err.println("CRITICAL ERROR: Failure to execute platform.");
            System.err.println("Details: " + e.getMessage());
        }
    }
}