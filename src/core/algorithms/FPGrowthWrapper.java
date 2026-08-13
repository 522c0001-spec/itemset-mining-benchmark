package core.algorithms;

import ca.pfv.spmf.algorithms.frequentpatterns.fpgrowth.AlgoFPGrowth;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

public class FPGrowthWrapper implements IMiningAlgorithm {

    @Override
    public long run(Map<String, Object> parameters, String inputPath, String outputPath) throws Exception {
        if (!parameters.containsKey("minSupport")) {
            throw new IllegalArgumentException("FPGrowth requires a 'minSupport' parameter.");
        }

        double minSupport = Double.parseDouble(parameters.get("minSupport").toString());

        AlgoFPGrowth fpGrowth = new AlgoFPGrowth();
        fpGrowth.runAlgorithm(inputPath, outputPath, minSupport);

        long itemsetCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(outputPath))) {
            while (reader.readLine() != null) {
                itemsetCount++;
            }
        }
        return itemsetCount;
    }
}