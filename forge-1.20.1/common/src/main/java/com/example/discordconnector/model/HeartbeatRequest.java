package com.example.discordconnector.model;

import java.util.List;

public record HeartbeatRequest(
    String serverId,
    List<String> players,
    long occurredAt
) {
}
