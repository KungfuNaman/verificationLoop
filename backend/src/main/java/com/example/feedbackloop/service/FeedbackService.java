package com.example.feedbackloop.service;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Service    // This annotation is used to mark the class as a service
public class FeedbackService {

    public static void main(String[] args) {
        // This is the main method of the FeedbackService class
        // It is used to test the methods of the FeedbackService class
        FeedbackService feedbackService = new FeedbackService();
        String code ="import java.util.Scanner;\n" +
                "\n" +
                "public class Fibonacci {\n" +
                "    public static void main(String[] args) {\n" +
                "        Scanner scanner = new Scanner(System.in);\n" +
                "        System.out.print(\"Enter the number of terms: \");\n" +
                "        int n = scanner.nextInt();\n" +
                "\n" +
                "        int[] fibSequence = fibonacci(n);\n" +
                "        System.out.print(\"Fibonacci Sequence: \");\n" +
                "        for (int i = 0; i < n; i++) {\n" +
                "            System.out.print(fibSequence[i] + \" \");\n" +
                "        }\n" +
                "    }\n" +
                "\n" +
                "    public static int[] fibonacci(int n) {\n" +
                "        int[] fibSequence = new int[n];\n" +
                "        fibSequence[0] = 0;\n" +
                "        fibSequence[1] = 1;\n" +
                "\n" +
                "        for (int i = 2; i < n; i++) {\n" +
                "            fibSequence[i] = fibSequence[i - 1] + fibSequence[i - 2];\n" +
                "        }\n" +
                "        return fibSequence;\n" +
                "    }\n" +
                "}\n";
//        feedbackService.generateAndVerify(code);
//        feedbackService.generateLlmOutput(code);
//          feedbackService.verifyWithInfer(code);                //DONE
//          feedbackService.verifyWithSymbolicExecution(code);      //DONE

//        feedbackService.verifyWithCheckstyle(code,"");         //DONE
    }
    public ResponseEntity<?> generateAndVerify (String code) {
        // This method is used to generate and verify the feedback  for the given url

        HashMap<String, Object> response=new HashMap<>();
        response.put("status", "success");
        response.put("code", code);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> generateLlmOutput (String code) {
        // This method is used to verify the feedback for the given url

        HashMap<String, Object> response=new HashMap<>();
        response.put("status", "success");
        response.put("code", code);

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> verifyWithInfer(String code, String fileName) {
        String filePath = fileName; // Path where the code will be saved

        // Write the submitted code to a file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(code);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to save the submitted code.");
        }

        // Compile and analyze the code
        try {
            Process compileProcess = Runtime.getRuntime().exec("javac " + filePath);
            int compileResult = compileProcess.waitFor();
            if (compileResult != 0) {
                String errors = readProcessOutput(compileProcess.getErrorStream());
                Map<String, Object> response = new HashMap<>();
                response.put("status", 1);
                response.put("message", "Error in compiling the java class file");
                response.put("result", errors); 
                return ResponseEntity.ok(response);
            }
            String compileLog = readProcessOutput(compileProcess.getInputStream());
            String[] command = {"docker", "exec", "infer_container", "infer", "run", "--", "javac", "/code/" + filePath};
            System.out.println("This is the command"+command);
            Process inferProcess = Runtime.getRuntime().exec(command);
            int inferResult = inferProcess.waitFor();
            if (inferResult != 0) {
                String errors = readProcessOutput(inferProcess.getErrorStream());
                Map<String, Object> response = new HashMap<>();
                response.put("status", 1);
                response.put("message", "Error in analysing code with infer");
                response.put("result", errors); 
                return ResponseEntity.ok(response);
            }
            String analysisResult = readProcessOutput(inferProcess.getInputStream());
           int count= countInferIssues(analysisResult);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 0);
            response.put("message", "Code analysed successfully with infer");
            response.put("result", analysisResult);
            response.put("issues", count);

            return ResponseEntity.ok(response);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("An error occurred during compilation or analysis.");
        }
    }

    // Helper method to read process output into a String
    private String readProcessOutput(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
        }
        return output.toString();
    }
    public ResponseEntity<?> verifyWithCheckstyle(String code,String fileName, String configContent) {
        String filePath = fileName;  // Path where the code will be saved
        String configFilePath = "checkstyle_config.xml"; // Path where the Checkstyle configuration will be saved

        // Write the submitted code to a file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(code);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to save the submitted code.");
        }

        // Write the configuration content to a file
        if(!configContent.isEmpty()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFilePath))) {
                writer.write(configContent);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().body("Failed to save the Checkstyle configuration.");
            }
        }else{
            configFilePath="default_checkstyle_config.xml";
        }

        // Compile the code to check syntax errors
        try {
            Process compileProcess = Runtime.getRuntime().exec("javac " + filePath);
            int compileResult = compileProcess.waitFor();
            if (compileResult != 0) {
                String errors = readProcessOutput(compileProcess.getErrorStream());
                Map<String, Object> response = new HashMap<>();
                response.put("status", 1);
                response.put("message", "Code analyzed unsuccessfully");
                response.put("result", errors); 
                return ResponseEntity.ok(response);
            }

            // Run Checkstyle on the code with the configuration file just created

            String checkstyleCommand = String.format("java -jar /app/checkstyle-8.41-all.jar -c %s %s", configFilePath, filePath);
            Process checkstyleProcess = Runtime.getRuntime().exec(checkstyleCommand);
            int checkstyleResult = checkstyleProcess.waitFor();
            String checkstyleOutput = readProcessOutput(checkstyleProcess.getInputStream());
            String checkstyleErrors = readProcessOutput(checkstyleProcess.getErrorStream());

            // Check if Checkstyle found any issues
            if (checkstyleResult != 0) {
                System.out.println("Checkstyle errors: " + checkstyleErrors);
                Map<String, Object> response = new HashMap<>();
                response.put("status", 1);
                response.put("message", "Code analyzed unsuccessfully");
                response.put("result", checkstyleErrors);
            }
            int issueCount = countCheckStyleIssues(checkstyleOutput);
            Map<String, Object> response = new HashMap<>();
            response.put("status",0);
            response.put("message", "Code analyzed successfully with Checkstyle.");
            response.put("result", checkstyleOutput);
            response.put("issues", issueCount);

            return ResponseEntity.ok(response);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Map<String, Object> response = new HashMap<>();
            response.put("status", 1);
            response.put("message", "Something is wrong with the class file or class name");
            response.put("result", ExceptionUtils.getStackTrace(e)); 
            return ResponseEntity.ok(response);
        }
    }
    private static int countJPFIssues(String output) {
        String[] lines = output.split("\\r?\\n");
        int count = 0;
        for (String line : lines) {
            if (line.startsWith("error")) {
                count++;
            }
        }
        return count;
    }
    private static int countInferIssues(String output) {
        // Pattern to search for the "Found X issue(s)"
        final String searchString = "Found ";
        int index = output.indexOf(searchString);

        if (index != -1) {
            // Extract substring starting after "Found "
            String substring = output.substring(index + searchString.length());
            // Assume the number of issues is the first integer after "Found"
            String[] words = substring.split("\\s+"); // Split by whitespace
            try {
                // Parse the first word which should be the number of issues
                return Integer.parseInt(words[0]);
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse number of issues: " + e.getMessage());
                return 0;
            }
        }
        return 0; // If "Found" is not in the string, return 0
    }
    private static int countCheckStyleIssues(String output) {
        String[] lines = output.split(System.lineSeparator());
        int count = 0;
        for (String line : lines) {
            if (line.contains("[WARN]") || line.contains("[ERROR]")) {
                count++;
            }
        }
        return count;
    }

    public ResponseEntity<?> verifyWithSymbolicExecution(String code,String fileName) {
        String filePath = fileName;  // Path where the code will be saved
        String className = fileName.replace(".java","");  // Assuming class name is Fibonacci

        // Write the submitted code to a file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(code);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to save the submitted code.");
        }

        // Compile the code to generate class file
        try {
            Process compileProcess = Runtime.getRuntime().exec("javac " + filePath);
            int compileResult = compileProcess.waitFor();
            if (compileResult != 0) {
                String errors = readProcessOutput(compileProcess.getErrorStream());
                Map<String, Object> response = new HashMap<>();
                response.put("status", 1);
                response.put("message", "Error in compiling the java class file");
                response.put("result", errors); 
                return ResponseEntity.ok(response);
            }

            // Create a .jpf file for Java Pathfinder
            String jpfFilePath = "verification.jpf";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(jpfFilePath))) {
                writer.write("target=" + className);
                writer.newLine();
                writer.write("classpath=.;");
            }

            // Execute Java Pathfinder with the .jpf file
            Process jpfProcess = Runtime.getRuntime().exec("java -jar /jpf/build/RunJPF.jar " + jpfFilePath);
            int jpfResult = jpfProcess.waitFor();
            if (jpfResult != 0) {
                String errors = readProcessOutput(jpfProcess.getErrorStream());
                Map<String, Object> response = new HashMap<>();
                response.put("status", 1);
                response.put("message", "Error in analysing the code with java pathfinder");
                response.put("result", errors); 
                return ResponseEntity.ok(response);
            }
            String analysisResult = readProcessOutput(jpfProcess.getInputStream());
            int count=countJPFIssues(analysisResult);
            Map<String, Object> response = new HashMap<>();
            response.put("status", 0);
            response.put("message", "Code analyzed successfully with JPF.");
            response.put("result", analysisResult);
            response.put("issues", count);
            return ResponseEntity.ok(response);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("An error occurred during compilation or analysis.");
        }
    }

}
