import pandas as pd
import matplotlib.pyplot as plt

# Load the CSV data into a DataFrame
df = pd.read_csv('./performance_costs_summary.csv')

# Data cleaning and conversion
df['CPUTime'] = df['CPUTime'].str.rstrip('%').astype(float) / 100
df['HeapUsage'] = df['HeapUsage'].str.rstrip('MB').astype(float)
df['GC_TotalPauseTime'] = df['GC_TotalPauseTime'].str.rstrip('ms').astype(float)
df['ConcurrencyCost'] = df['ConcurrencyCost'].str.rstrip('ms').astype(float)
df['IOWrittenDuration'] = df['IOWrittenDuration'].str.rstrip('ms').astype(float)
df['IOReadDuration'] = df['IOReadDuration'].str.rstrip('ms').astype(float)
df['IOWritten'] = df['IOWritten'].str.rstrip('bytes').astype(float)
df['IORead'] = df['IORead'].str.rstrip('bytes').astype(float)

# Set the plot style
plt.style.use('_mpl-gallery-nogrid')

# Create a figure and a set of subplots
fig, axes = plt.subplots(nrows=3, ncols=2, figsize=(15, 15))
fig.suptitle('Performance Analysis of DDMin Application', fontsize=16)

# Define the model indices starting from 1
#model_indices = range(1, len(df) + 1)
#
#def adjust_xaxis(ax, model_indices):
#    ax.set_xticks(model_indices)
#    ax.set_xlim(0.5, len(df) + 0.5)

# Plot CPU Time Usage
axes[0, 0].bar(df.index, df['CPUTime'], color='skyblue')
axes[0, 0].set_xlabel('CPU Time Usage', labelpad=20)
#adjust_xaxis(axes[0, 0], model_indices)
axes[0, 0].set_ylabel('CPU Time (%)')

# Plot Heap Usage
axes[0, 1].bar(df.index, df['HeapUsage'], color='lightgreen')
axes[0, 1].set_xlabel('Heap Usage', labelpad=20)
#adjust_xaxis(axes[0, 1], model_indices)
axes[0, 1].set_ylabel('Heap Usage (MB)')

# Plot GC Total Pause Time
axes[1, 0].bar(df.index, df['GC_TotalPauseTime'], color='salmon')
axes[1, 0].set_xlabel('GC Total Pause Time', labelpad=20)
#adjust_xaxis(axes[1, 0], model_indices)
axes[1, 0].set_ylabel('Pause Time (ms)')

# Plot Concurrency Cost
axes[1, 1].bar(df.index, df['ConcurrencyCost'], color='gold')
axes[1, 1].set_xlabel('Concurrency Cost', labelpad=20)
#adjust_xaxis(axes[1, 1], model_indices)
axes[1, 1].set_ylabel('Cost (ms)')

# Plot I/O Written Duration
axes[2, 0].bar(df.index, df['IOWrittenDuration'], color='purple')
axes[2, 0].set_xlabel('I/O Written Duration', labelpad=20)
#adjust_xaxis(axes[2, 0], model_indices)
axes[2, 0].set_ylabel('Duration (ms)')

# Plot I/O Read Duration
axes[2, 1].bar(df.index, df['IOReadDuration'], color='orange')
axes[2, 1].set_xlabel('I/O Read Duration', labelpad=20)
#adjust_xaxis(axes[2, 1], model_indices)
axes[2, 1].set_ylabel('Duration (ms)')

# Adjust layout
plt.tight_layout(rect=[0, 0.03, 1, 0.95])

# Save the figure to a file
plt.savefig('./performance_costs_graphs/performance_costs_graphs.png')

# Show the plots
#plt.show()

