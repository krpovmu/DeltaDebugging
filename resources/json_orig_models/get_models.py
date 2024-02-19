import json
import os
import argparse
import re

# Create argument parser to accept a file path
parser = argparse.ArgumentParser(description='Process JSON file.')
parser.add_argument('json_file', type=str, help='Path to the JSON file')
args = parser.parse_args()

input_file_path = args.json_file
output_directory = './models/'

# Ensure the output directory exists
if not os.path.exists(output_directory):
    os.makedirs(output_directory)

# Read and process each JSON object from the file
with open(input_file_path, 'r') as file:
    for line in file:
        try:
            item = json.loads(line)
            # Check if the 'sat' field is -1
            if item.get('sat') == -1:
                # Find all lines starting with 'pred' and extract the next word
                pred_words = re.findall(r'\bpred (\w+)', item.get('code'))
                
                # Extract the 'code' field and prepare content to write
                code_content = item.get('code')
                if pred_words:
                    # Add the special line at the end
                    pred_line = "run {" + " ".join(pred_words) + "} for 10"
                    code_content += "\n" + pred_line
                
                # Generate a filename based on a unique identifier (e.g., '_id' field)
                file_name = f"{item.get('_id')}.als"
                output_path = os.path.join(output_directory, file_name)
                
                # Write the content to a new file
                with open(output_path, 'w') as output_file:
                    output_file.write(code_content)
        except json.JSONDecodeError as e:
            print(f"Error decoding JSON from line: {e}")

print(f"Files have been created in {output_directory}")

