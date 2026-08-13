package core.algorithms;

import java.util.Map;

/**
 * A universal interface for all data mining algorithms integrated into the benchmark platform.
 */
public interface IMiningAlgorithm {

    /**
     * Executes the mining algorithm.
     *
     * @param parameters A generic map containing algorithm-specific parameters (e.g., "minSupport", "k")
     * @param inputPath  The absolute or relative path to the input dataset file
     * @param outputPath The absolute or relative path where the output itemsets should be saved
     * @return The total number of itemsets mined (used for sanity checking and reporting)
     * @throws Exception If any issue occurs during file I/O or algorithm execution
     */
    long run(Map<String, Object> parameters, String inputPath, String outputPath) throws Exception;

}