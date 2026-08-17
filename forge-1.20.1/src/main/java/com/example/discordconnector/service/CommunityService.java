package com.example.discordconnector.service;

import com.example.discordconnector.DiscordConnectorForge;
import com.example.discordconnector.model.PlayerInfo;

public class CommunityService {
  public void onPlayerJoin(PlayerInfo playerInfo) {
    DiscordConnectorForge.LOGGER.info(
        "Player joined: server_id={}, uuid={}, name={}",
        playerInfo.serverId(),
        playerInfo.uuid(),
        playerInfo.name());
  }

  public void onPlayerLeave(PlayerInfo playerInfo) {
    DiscordConnectorForge.LOGGER.info(
        "Player left: server_id={}, uuid={}, name={}",
        playerInfo.serverId(),
        playerInfo.uuid(),
        playerInfo.name());
  }
}
