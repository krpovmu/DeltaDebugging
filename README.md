# Delta Debugging for Alloy Models

## Overview

This Java project implements a delta debugging algorithm tailored for analyzing and isolating faults in Alloy models. It's composed of several key components that work together to minimize the input data causing test failures in Alloy models.

## Key Components

- `AlloyManager`: The main class that serves as the entry point of the application. It handles command-line arguments and orchestrates the overall debugging process.
- `AbstractDDPlus`: This class implements the core logic of the delta debugging algorithms, providing methods for both the basic and extended versions of delta debugging.
- `DDPlusTest`: Implements the `IDDPlusTest` interface, providing the testing logic specific to Alloy models.

## Setup and Installation

### Prerequisites

- Java JDK 11 or higher installed.
- An IDE such as IntelliJ IDEA or Eclipse (optional but recommended for ease of development).

### Installation Steps

1. Clone the GitHub repository:
   ```bash
   git clone https://github.com/your-github-repository-url.git
   cd path-to-your-project

2. If using an IDE, import the project into your IDE of choice.
3. Ensure that Java JDK 11 or higher is set as the project's SDK.

## Running the Application

To run the `AlloyManager` class and start the delta debugging process, follow these steps:

1. Navigate to the `AlloyManager` class in your IDE or command line interface.
2. Execute the `main` method, providing necessary command-line arguments:
   - `-i` or `--input`: Specifies the path to the Alloy model file.
   - `-f` or `--facts`: Opt to analyze errors related to facts in the model.
   - `-p` or `--predicates`: Opt to analyze errors related to predicates in the model.
   - `-t` or `--trace`: Enable trace logging for detailed analysis output.

   **Example Command:**
   ```bash
   java -jar your-jar-file.jar --input "path/to/alloy-model.als" --facts

## Usage

This section provides detailed instructions on how to use the application. Follow these steps to run different modes and utilize various options:

1. **Facts Analysis Mode**:
   - Use this mode to analyze errors related to facts in the Alloy model.
   - Example command:
     ```bash
     java -jar your-jar-file.jar --input "path/to/alloy-model.als" --facts
     ```

2. **Predicates Analysis Mode**:
   - Use this mode to analyze errors related to predicates in the model.
   - Example command:
     ```bash
     java -jar your-jar-file.jar --input "path/to/alloy-model.als" --predicates
     ```

3. **Trace Logging**:
   - Enable trace logging to get detailed analysis output.
   - Example command:
     ```bash
     java -jar your-jar-file.jar --input "path/to/alloy-model.als" --trace
     ```
Adjust the command-line arguments as per your requirements to analyze different aspects of the Alloy model.

## Contributing

We encourage contributions to this project. If you'd like to contribute, please follow these steps:

1. **Fork the Repository**:
   - Create a fork of the main repository on your GitHub account.

2. **Create a Feature Branch**:
   - `git checkout -b feature-branch`

3. **Commit Your Changes**:
   - Make your changes and commit them: `git commit -am 'Add some feature'`

4. **Push to the Branch**:
   - `git push origin feature-branch`

5. **Open a Pull Request**:
   - Go to the original repository and open a pull request from your feature branch.

## Documentation

For detailed information about the algorithms and methodologies used in this project, refer to the `docs` directory within the repository.

## License

This project is licensed under [Your License Name Here]. See the LICENSE.md file in the repository for detailed terms and conditions.

## Contact

For any inquiries or further discussion about the project, please reach out to:

- **Email**: [your-email@example.com]
- **LinkedIn**: [your-linkedin-profile-link]
- **GitHub Profile**: [@YourGitHubUsername](https://github.com/YourGitHubUsername)
