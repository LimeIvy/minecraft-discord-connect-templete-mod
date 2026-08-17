package com.example.discordconnector.model;

import java.util.UUID;

public record LinkCodeRequest(
    String serverId,
    UUID minecraftUuid,
    String minecraftName
) {
  public static LinkCodeRequest from(PlayerInfo playerInfo) {
    return new LinkCodeRequest(
        playerInfo.serverId(),
        playerInfo.uuid(),
        playerInfo.name());
  }
}
