package com.example.demo.service;

import com.example.demo.dto.SolutionRequest;
import com.example.demo.dto.WebhookRequest;
import com.example.demo.dto.WebhookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class SqlSolverService {

    private final RestTemplate restTemplate;

    private static final String GENERATE_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

    public void solve() {
        log.info("Starting SQL Solver Flow...");

        // Step 1: Generate Webhook
        WebhookRequest webhookRequest = new WebhookRequest("Shreyansh Kumar", "22bbs0234", "shreyansh@example.com");
        log.info("Sending Webhook Request: {}", webhookRequest);

        ResponseEntity<WebhookResponse> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(GENERATE_WEBHOOK_URL, webhookRequest, WebhookResponse.class);
        } catch (Exception e) {
            log.error("Failed to generate webhook: {}", e.getMessage());
            return;
        }

        WebhookResponse webhookResponse = responseEntity.getBody();
        if (webhookResponse == null) {
            log.error("Received null response from generateWebhook");
            return;
        }

        log.info("Received Webhook Response: {}", webhookResponse);
        String webhookUrl = webhookResponse.getWebhookUrl();
        String accessToken = webhookResponse.getAccessToken();

        // Step 2: Construct SQL Query (Question 2)
        // Using MySQL syntax as assumed standard for this test
        String sqlQuery = "SELECT " +
                "d.DEPARTMENT_NAME, " +
                "AVG(TIMESTAMPDIFF(YEAR, e.DOB, NOW())) AS AVERAGE_AGE, " +
                "SUBSTRING_INDEX(GROUP_CONCAT(CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) SEPARATOR ', '), ', ', 10) AS EMPLOYEE_LIST " +
                "FROM DEPARTMENT d " +
                "JOIN EMPLOYEE e ON d.DEPARTMENT_ID = e.DEPARTMENT " +
                "JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID " +
                "WHERE p.AMOUNT > 70000 " +
                "GROUP BY d.DEPARTMENT_ID, d.DEPARTMENT_NAME " +
                "ORDER BY d.DEPARTMENT_ID DESC";

        log.info("Constructed SQL Query: {}", sqlQuery);

        // Step 3: Submit Solution
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken); // As per instructions: Authorization: <accessToken> (no Bearer prefix mentioned, but usually implied. Prompt says: Authorization : < accessToken >. I will send raw token as requested).

        SolutionRequest solutionRequest = new SolutionRequest(sqlQuery);
        HttpEntity<SolutionRequest> requestEntity = new HttpEntity<>(solutionRequest, headers);

        try {
            log.info("Submitting solution to: {}", webhookUrl);
            ResponseEntity<String> submitResponse = restTemplate.postForEntity(webhookUrl, requestEntity, String.class);
            log.info("Submission Response Code: {}", submitResponse.getStatusCode());
            log.info("Submission Response Body: {}", submitResponse.getBody());
        } catch (Exception e) {
            log.error("Failed to submit solution: {}", e.getMessage());
        }
    }
}
