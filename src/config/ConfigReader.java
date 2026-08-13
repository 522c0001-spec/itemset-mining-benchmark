package config;

import core.AlgorithmRunner;
import org.yaml.snakeyaml.Yaml;
import utils.CSVWriter;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class ConfigReader {
    public static void main(String[] args) {
        System.out.println("=====================================");
        System.out.println("BENCHMARK PLATFORM - STARTING BATCH RUN");
        System.out.println("=====================================");

        Yaml yaml = new Yaml();
        try (InputStream in = Files.newInputStream(Paths.get("config.yaml"))) {
            Map<String, Object> config = yaml.load(in);

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> experiments = (List<Map<String, Object>>) config.get("experiments");

            AlgorithmRunner runner = new AlgorithmRunner();
            CSVWriter csvWriter = new CSVWriter(); // Assuming your CSVWriter is fully intact

            for (Map<String, Object> experiment : experiments) {
                String experimentName = (String) experiment.get("name");
                String dataset = (String) experiment.get("dataset");
                String outputPath = "output/raw_itemsets.txt";

                @SuppressWarnings("unchecked")
                List<Map<String, Object>> algorithms = (List<Map<String, Object>>) experiment.get("algorithms");

                for (Map<String, Object> algoConfig : algorithms) {

                    String className = (String) algoConfig.get("className");

                    // Safely extract classDir (will be null if not defined in YAML)
                    String classDir = (String) algoConfig.get("classDir");

                    @SuppressWarnings("unchecked")
                    Map<String, Object> parameters = (Map<String, Object>) algoConfig.get("parameters");

                    // Execute via the updated reflection engine
                    Map<String, Object> metrics = runner.runAlgorithm(className, classDir, parameters, dataset, outputPath);

                    if (metrics != null) {
                        csvWriter.writeResult(experimentName, dataset, metrics);
                    }
                }
            }
            System.out.println("\n=====================================");
            System.out.println("BENCHMARK COMPLETE. Check output/results.csv");
            System.out.println("=====================================");

        } catch (Exception e) {
            System.err.println("CRITICAL: Failed to read config.yaml");
            e.printStackTrace();
        }
    }
}