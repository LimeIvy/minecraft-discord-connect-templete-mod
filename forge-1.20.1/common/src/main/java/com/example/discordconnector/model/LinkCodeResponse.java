package com.example.discordconnector.model;

public record LinkCodeResponse(
    String code,
    long expiresAt
) {
}
