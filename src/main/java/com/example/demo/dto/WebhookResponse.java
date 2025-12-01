package com.example.demo.dto;

import lombok.Data;

@Data
public class WebhookResponse {
    @com.fasterxml.jackson.annotation.JsonAlias({ "webhook", "webhook_url", "webhookUrl" })
    private String webhookUrl;
    @com.fasterxml.jackson.annotation.JsonAlias({ "accessToken", "access_token", "token" })
    private String accessToken;
}
