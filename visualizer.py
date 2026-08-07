import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import os

csv_path = os.path.join("output", "results.csv")

def load_benchmark_data():
    print("=====================================")
    print("Running Visualizer...")
    print("=====================================")
    
    try:
        df = pd.read_csv(csv_path)
        print(f"Successfully loaded {len(df)} rows of benchmark data\n")
        return df
    except FileNotFoundError:
        print(f"Error: Canot find '{csv_path}'.")
        return None

def export_to_excel(df):
    print("* Exporting dataset to Excel format...")
    excel_path = os.path.join("output", "results_report.xlsx")
    df.to_excel(excel_path, index=False)
    print(f"  -> Saved to {excel_path}")

def generate_execution_time_chart(df):
    print("* Generating Execution Time charts (PNG & PDF)...")
    sns.set_theme(style="whitegrid")
    plt.figure(figsize=(8, 6))
    
    sns.barplot(data=df, x="Algorithm", y="Execution_Time_ms", hue="Algorithm", palette="viridis", legend=False)
    
    plt.title("Algorithm Execution Time Comparison", fontsize=14, fontweight="bold", pad=15)
    plt.xlabel("Data Mining Algorithm", fontsize=12)
    plt.ylabel("Execution Time (milliseconds)", fontsize=12)
    
    # Save as PNG and PDF to fulfill requirements
    plt.savefig(os.path.join("output", "execution_time_chart.png"), dpi=300, bbox_inches="tight")
    plt.savefig(os.path.join("output", "execution_time_chart.pdf"), dpi=300, bbox_inches="tight")
    plt.clf()

def generate_memory_chart(df):
    print("* Generating Memory Consumption charts (PNG & PDF)...")
    sns.set_theme(style="whitegrid")
    plt.figure(figsize=(8, 6))
    
    # Using a different color palette ('magma') to distinguish from the time chart
    sns.barplot(data=df, x="Algorithm", y="Memory_Used_MB", hue="Algorithm", palette="magma", legend=False)
    
    plt.title("Algorithm Memory Consumption Comparison", fontsize=14, fontweight="bold", pad=15)
    plt.xlabel("Data Mining Algorithm", fontsize=12)
    plt.ylabel("Peak Memory Used (MB)", fontsize=12)
    
    # Save as PNG and PDF
    plt.savefig(os.path.join("output", "memory_consumption_chart.png"), dpi=300, bbox_inches="tight")
    plt.savefig(os.path.join("output", "memory_consumption_chart.pdf"), dpi=300, bbox_inches="tight")
    plt.clf()

if __name__ == "__main__":
    benchmark_df = load_benchmark_data()
    
    if benchmark_df is not None:
        export_to_excel(benchmark_df)
        generate_execution_time_chart(benchmark_df)
        generate_memory_chart(benchmark_df)
        
        print("\n=====================================")
        print("Successfully visualized and exported.")
        print("=====================================")