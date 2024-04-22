# FeedBackLoopFullStack

This project introduces an iterative tool designed to verify LLM (Large Language Model) code completions using CheckStyle, Infer, and Java NASA Path Finder in a continuous feedback loop.

## Getting Started

### Prerequisites

Ensure that Docker is installed on your machine. You can download it from the Docker website.

### Installation

1. **Open a terminal and navigate to the root of the project folder.**  
   Verify that you are in the correct directory by typing `ls`. Confirm the presence of the `docker-compose.yml` file.

2. **Start your Docker daemon**  
   This step depends on your operating system, but generally, Docker starts automatically after installation.

3. **Run Docker Compose**  
   Use the following command to start all the Docker components:
   ```bash
   docker-compose up --build

### Usage

1. **Access the Interface**  
    Open your web browser and navigate to:
    ```bash
   http://localhost:3000

2. **Demo and Custom Code Verification**  
To test with a demo program, click on the designated button to load the sample code and then click 'Submit' to see the results.
Ensure that the Java class name is correct; otherwise, the code verification might fail.
3. **Iterative Feedback and Visualization**  
   After submitting the code, the issues identified by the verifier will be displayed. You can use these details to refine your code. Once adjustments are made, resubmit the code to see if the issues have been resolved.
   Visualizations on the right side of the interface help track the number of issues over each iteration.
3. **Resetting Iterations**  
To begin a new iteration cycle, click on the 'Clear Iterations' button to reset the process.