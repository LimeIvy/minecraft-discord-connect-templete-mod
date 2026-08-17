package com.example.discordconnector.model;

import java.util.UUID;

public record JoinEventRequest(
    UUID uuid,
    String name,
    String serverId
) {
  public static JoinEventRequest from(PlayerInfo playerInfo) {
    return new JoinEventRequest(playerInfo.uuid(), playerInfo.name(), playerInfo.serverId());
  }
}
