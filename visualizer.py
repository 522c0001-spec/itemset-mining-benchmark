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
        print(f"Error: Cannot find '{csv_path}'.")
        return None

def export_to_excel(df):
    print("* Exporting dataset to Excel format...")
    excel_path = os.path.join("output", "results_report.xlsx")
    df.to_excel(excel_path, index=False)
    print(f"  -> Saved to {excel_path}")

def generate_dynamic_charts(df):
    print("* Generating Dynamic Line charts (PNG & PDF)...")
    sns.set_theme(style="whitegrid")
    
    df_plot = df.copy()
    
    # Safely convert time
    if "Execution_Time_ms" in df_plot.columns:
        df_plot['Execution_Time_s'] = df_plot['Execution_Time_ms'] / 1000.0
    else:
        print("Error: 'Execution_Time_ms' column missing from CSV.")
        return

    # Visual mapping
    markers = {'Apriori': 'D', 'FPGrowth': '^', 'ECLAT': 'X'}
    colors = {'Apriori': '#C44E52', 'FPGrowth': '#55A868', 'ECLAT': '#8172B2'}
    target_algos = ['Apriori', 'FPGrowth', 'ECLAT']
    
    # Ensure Dataset column exists, otherwise fallback to a default list
    dataset_col = "Dataset" if "Dataset" in df_plot.columns else None
    datasets = df_plot[dataset_col].unique() if dataset_col else ["Benchmark"]
    for dataset in datasets:
        if dataset_col:
            dataset_name = os.path.basename(str(dataset)).split('.')[0].capitalize()
            dataset_df = df_plot[df_plot[dataset_col] == dataset].sort_values(by="MinSupport", ascending=False)
        else:
            dataset_name = "Overall"
            dataset_df = df_plot.sort_values(by="MinSupport", ascending=False)
            
        print(f"  -> Rendering charts for: {dataset_name}")

        # ---------------------------------------------------------
        # 1. Execution Time Chart
        # ---------------------------------------------------------
        plt.figure(figsize=(9, 6))
        for algo in target_algos:
            algo_df = dataset_df[dataset_df["Algorithm"].str.contains(algo, case=False, na=False)]
            if not algo_df.empty:
                plt.plot(algo_df["MinSupport"], algo_df['Execution_Time_s'], 
                         marker=markers.get(algo, 'o'), color=colors.get(algo, 'black'), 
                         linewidth=2.5, markersize=8, label=algo)
        
        plt.title(f'Performance (Execution Time)\n"{dataset_name}" dataset', fontsize=16, pad=15)
        plt.xlabel('minsup', fontsize=12, fontweight='bold', fontstyle='italic')
        plt.ylabel('Execution time (s)', fontsize=12, fontweight='bold')
        
        plt.gca().invert_xaxis() # High to low X-axis
        plt.legend(title='Algorithm', loc='center left', bbox_to_anchor=(1, 0.5), frameon=True, edgecolor='black')
        
        plt.savefig(os.path.join("output", f"execution_time_line_{dataset_name}.png"), dpi=300, bbox_inches="tight")
        plt.savefig(os.path.join("output", f"execution_time_line_{dataset_name}.pdf"), dpi=300, bbox_inches="tight")
        plt.close()

        # ---------------------------------------------------------
        # 2. Memory Consumption Chart
        # ---------------------------------------------------------
        if "Memory_Used_MB" in df_plot.columns:
            plt.figure(figsize=(9, 6))
            for algo in target_algos:
                algo_df = dataset_df[dataset_df["Algorithm"].str.contains(algo, case=False, na=False)]
                if not algo_df.empty:
                    plt.plot(algo_df["MinSupport"], algo_df['Memory_Used_MB'], 
                             marker=markers.get(algo, 'o'), color=colors.get(algo, 'black'), 
                             linewidth=2.5, markersize=8, label=algo)
                             
            plt.title(f'Performance (Memory Consumption)\n"{dataset_name}" dataset', fontsize=16, pad=15)
            plt.xlabel('minsup', fontsize=12, fontweight='bold', fontstyle='italic')
            plt.ylabel('Peak Memory Used (MB)', fontsize=12, fontweight='bold')
            
            plt.gca().invert_xaxis()
            plt.legend(title='Algorithm', loc='center left', bbox_to_anchor=(1, 0.5), frameon=True, edgecolor='black')
            
            plt.savefig(os.path.join("output", f"memory_consumption_line_{dataset_name}.png"), dpi=300, bbox_inches="tight")
            plt.savefig(os.path.join("output", f"memory_consumption_line_{dataset_name}.pdf"), dpi=300, bbox_inches="tight")
            plt.close()

if __name__ == "__main__":
    benchmark_df = load_benchmark_data()
    if benchmark_df is not None:
        export_to_excel(benchmark_df)
        generate_dynamic_charts(benchmark_df)
        
        print("\n=====================================")
        print("Successfully visualized and exported.")
        print("=====================================")