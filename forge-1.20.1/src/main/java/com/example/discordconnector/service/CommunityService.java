package com.example.discordconnector.service;

import com.example.discordconnector.DiscordConnectorForge;
import com.example.discordconnector.api.ApiClient;
import com.example.discordconnector.model.JoinEventRequest;
import com.example.discordconnector.model.LeaveEventRequest;
import com.example.discordconnector.model.PlayerInfo;

public class CommunityService {
  private final ApiClient apiClient;

  public CommunityService(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public void onPlayerJoin(PlayerInfo playerInfo) {
    DiscordConnectorForge.LOGGER.info(
        "Player joined: server_id={}, uuid={}, name={}",
        playerInfo.serverId(),
        playerInfo.uuid(),
        playerInfo.name());
    apiClient.sendPlayerJoin(JoinEventRequest.from(playerInfo));
  }

  public void onPlayerLeave(PlayerInfo playerInfo) {
    DiscordConnectorForge.LOGGER.info(
        "Player left: server_id={}, uuid={}, name={}",
        playerInfo.serverId(),
        playerInfo.uuid(),
        playerInfo.name());
    apiClient.sendPlayerLeave(LeaveEventRequest.from(playerInfo));
  }
}
