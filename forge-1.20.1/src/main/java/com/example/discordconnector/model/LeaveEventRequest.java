package com.example.discordconnector.model;

import java.util.UUID;

public record LeaveEventRequest(
    String serverId,
    UUID minecraftUuid,
    long occurredAt
) {
  public static LeaveEventRequest from(PlayerInfo playerInfo, long occurredAt) {
    return new LeaveEventRequest(
        playerInfo.serverId(),
        playerInfo.uuid(),
        occurredAt);
  }
}
