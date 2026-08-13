package core.algorithms;

import ca.pfv.spmf.algorithms.frequentpatterns.apriori_simple.AlgoApriori;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

public class AprioriWrapper implements IMiningAlgorithm {

    @Override
    public long run(Map<String, Object> parameters, String inputPath, String outputPath) throws Exception {
        // Validate and extract specific parameters
        if (!parameters.containsKey("minSupport")) {
            throw new IllegalArgumentException("Apriori requires a 'minSupport' parameter.");
        }

        // Safely parse to double (handles both int and double YAML inputs)
        double minSupport = Double.parseDouble(parameters.get("minSupport").toString());

        // Run the SPMF algorithm
        AlgoApriori apriori = new AlgoApriori();
        apriori.runAlgorithm(minSupport, inputPath, outputPath);

        // Perform the itemset sanity check
        long itemsetCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(outputPath))) {
            while (reader.readLine() != null) {
                itemsetCount++;
            }
        }
        return itemsetCount;
    }
}