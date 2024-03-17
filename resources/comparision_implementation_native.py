import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

# Load the data
df = pd.read_csv('comparision_implementation_native.csv')

# Convert time execution fields from strings to numerical values (milliseconds)
df['TimeExecImplMs'] = df['TimeExecImpl'].str.replace('ms', '').astype(float)
df['TimeExecNativeMs'] = df['TimeExecNative'].str.replace('ms', '').astype(float)

# Determine correctness of the custom implementation
df['Correctness'] = df['ResultImp'] == df['ResultNative']

# Plotting the comparison of execution times
plt.figure(figsize=(14, 8))

# Bar positions
bar_width = 0.35
index = np.arange(len(df))

# Bars for execution times
plt.bar(index, df['TimeExecImplMs'], bar_width, label='Custom Implementation', color='blue')
plt.bar(index + bar_width, df['TimeExecNativeMs'], bar_width, label='Native Tool', color='orange')

plt.xlabel('Models')
plt.ylabel('Execution Time (ms)')
plt.title('Execution Time Comparison')
plt.xticks(index + bar_width / 2, df['FileName'], rotation=90)
plt.legend()

plt.tight_layout()
#plt.show()
# Save the figure to a file for execution comparision
plt.savefig('./comparision_implementation_native/execution_time_graph.png')

# Plotting the correctness comparison
correct_counts = df['Correctness'].value_counts()

plt.figure(figsize=(6, 6))
correct_counts.plot(kind='bar', color=['green', 'red'])
plt.title('Correctness of Implementation vs Native (Alloy Tools MinSat)')
plt.xticks([0, 1], ['Correct', 'Incorrect'], rotation=0)
plt.ylabel('Number of Models')
#plt.show()
# Save the figure to a file for execution comparision
plt.savefig('./comparision_implementation_native/correctness_graph.png')

