package com.example.discordconnector.model;

public record ServerEventRequest(
    String serverId,
    long occurredAt
) {
}
