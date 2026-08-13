# Frequent Itemset Mining Benchmark Platform
> **HOW TO DOWNLOAD:** 
> Please go to the **[Releases page](../../releases/latest)** (or click "Releases" on the right sidebar) and download the `.zip` file. It contains the compiled `.jar` and all necessary files ready to run. (You only need the prerequisites listed below such as JDK 26 and Python along with its dependencies.
## Prerequisites
- **Java Development Kit (JDK) 26:** This artifact was compiled using OpenJDK 26.0.1. You must use Java 26 or higher to execute the `.jar` file.
- **Python 3.8+** (Dependencies: `pip install pandas matplotlib seaborn openpyxl`)

## How to Run:

1. **Configure Experiments**:
   Edit `config.yaml` to set your desired dataset and minimum support thresholds.

2. **Run the Benchmark Engine (Java)**:
   Open your terminal and execute the `.jar`. If Java 26 is not your default system environment variable, you must use the path to your Java 26 executable:
   ### Example:
   ```
   C:\Users\LENOVO\.jdks\openjdk-26.0.1\bin\java.exe -jar BenchmarkPlatform.jar
    ```
   Results will be saved to output/results.csv.

4. **Generate Visualization (Python)**:

    Run this command on your terminal:
    ```
    python visualizer.py
    ```
    Results will be saved to output as PNG and PDF files.
