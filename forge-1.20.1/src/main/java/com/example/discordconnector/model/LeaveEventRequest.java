package com.example.discordconnector.model;

import java.util.UUID;

public record LeaveEventRequest(
    UUID uuid,
    String name,
    String serverId
) {
  public static LeaveEventRequest from(PlayerInfo playerInfo) {
    return new LeaveEventRequest(playerInfo.uuid(), playerInfo.name(), playerInfo.serverId());
  }
}
