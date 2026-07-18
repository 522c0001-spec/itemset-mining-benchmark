# Itemset Mining Benchmark Platform


An automated benchmark command-line software to evaluate and compare the efficiency of different data mining algorithms.

## Current Progress: Command-line System and CSV Output
* **Dynamic Configuration:** Experiential parameters are read dynamically from `config.yaml`.
* **Algorithmic Execution:** Runs Apriori, FP-Growth, and ECLAT through the SPMF library wrapper.
* **Performance Tracking:** Measures real-time execution duration (ms) and memory footprint (MB).
* **Metric Exporter:** Automates row persistence directly to `output/results.csv`.

## System Requirements
* **Java SDK 25 or higher** is required to run the compiled program.

## How to Run
    1. Open a terminal or command prompt inside the project directory.
    2. Execute the benchmark suite using the standalone engine:
   ```bash
   java -jar BenchmarkPlatform.jar