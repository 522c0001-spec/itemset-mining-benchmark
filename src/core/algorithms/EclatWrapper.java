package core.algorithms;

import ca.pfv.spmf.algorithms.frequentpatterns.eclat.AlgoEclat;
import ca.pfv.spmf.input.transaction_database_list_integers.TransactionDatabase;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

public class EclatWrapper implements IMiningAlgorithm {

    @Override
    public long run(Map<String, Object> parameters, String inputPath, String outputPath) throws Exception {
        if (!parameters.containsKey("minSupport")) {
            throw new IllegalArgumentException("ECLAT requires a 'minSupport' parameter.");
        }

        double minSupport = Double.parseDouble(parameters.get("minSupport").toString());

        // ECLAT requires loading the database into memory first
        TransactionDatabase database = new TransactionDatabase();
        database.loadFile(inputPath);

        AlgoEclat eclat = new AlgoEclat();
        eclat.runAlgorithm(outputPath, database, minSupport, false);

        long itemsetCount = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(outputPath))) {
            while (reader.readLine() != null) {
                itemsetCount++;
            }
        }
        return itemsetCount;
    }
}