package com.example.feedbackloop.controller;

import com.example.feedbackloop.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
public class FeedbackRoutes {

    @Autowired
    FeedbackService feedbackService;

    @PostMapping("/generateAndVerify")
    public ResponseEntity<?> generateAndVerify(HttpServletRequest request,
                                               @RequestBody(required = false) HashMap<String,Object> requestBody) throws Exception {
        System.out.println("request received");
        return feedbackService.generateAndVerify((String) requestBody.get("code"));
    }
    @PostMapping("/generateLlmOutput")
    public ResponseEntity<?> generateLlmOutput(HttpServletRequest request,
                                               @RequestBody(required = false) HashMap<String,Object> requestBody) throws Exception {
        System.out.println("request received");
        return feedbackService.generateLlmOutput((String) requestBody.get("code"));
    }

    @PostMapping("/verifyWithInfer")
    public ResponseEntity<?> verifyWithInfer(HttpServletRequest request,
                                               @RequestBody(required = false) HashMap<String,Object> requestBody) throws Exception {
        System.out.println("request received");

        return feedbackService.verifyWithInfer((String) requestBody.get("code"),(String) requestBody.get("fileName"));
    }
    @PostMapping("/verifyWithSymbolicExecution")
    public ResponseEntity<?> verifyWithSymbolicExecution(HttpServletRequest request,
                                               @RequestBody(required = false) HashMap<String,Object> requestBody) throws Exception {
        System.out.println("request received");

        return feedbackService.verifyWithSymbolicExecution((String) requestBody.get("code"),(String) requestBody.get("fileName"));
    }
    @PostMapping("/verifyWithCheckstyle")
    public ResponseEntity<?> verifyWithCheckstyle(HttpServletRequest request,
                                               @RequestBody(required = false) HashMap<String,Object> requestBody) throws Exception {
        System.out.println("request received");

        return feedbackService.verifyWithCheckstyle((String) requestBody.get("code"),(String) requestBody.get("fileName"),(String) requestBody.get("checkStyleConfig"));
    }
}
