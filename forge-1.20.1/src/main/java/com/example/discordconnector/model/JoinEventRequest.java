package com.example.discordconnector.model;

import java.util.UUID;

public record JoinEventRequest(
    String serverId,
    UUID minecraftUuid,
    String minecraftName,
    long occurredAt
) {
  public static JoinEventRequest from(PlayerInfo playerInfo, long occurredAt) {
    return new JoinEventRequest(
        playerInfo.serverId(),
        playerInfo.uuid(),
        playerInfo.name(),
        occurredAt);
  }
}
