package com.example.discordconnector.model;

import java.util.UUID;

public record PlayerInfo(
    UUID uuid,
    String name,
    String serverId
) {
}
