package com.example.discordconnector.service;

import com.example.discordconnector.api.ApiClient;
import com.example.discordconnector.model.JoinEventRequest;
import com.example.discordconnector.model.LeaveEventRequest;
import com.example.discordconnector.model.PlayerInfo;
import java.time.Instant;
import java.util.logging.Logger;

public class CommunityService {
  private static final Logger LOGGER = Logger.getLogger(CommunityService.class.getName());
  private final ApiClient apiClient;

  public CommunityService(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public void onPlayerJoin(PlayerInfo playerInfo) {
    LOGGER.info(() -> String.format(
        "Player joined: server_id=%s, uuid=%s, name=%s",
        playerInfo.serverId(),
        playerInfo.uuid(),
        playerInfo.name()));
    apiClient.sendPlayerJoin(JoinEventRequest.from(playerInfo, occurredAt()));
  }

  public void onPlayerLeave(PlayerInfo playerInfo) {
    LOGGER.info(() -> String.format(
        "Player left: server_id=%s, uuid=%s, name=%s",
        playerInfo.serverId(),
        playerInfo.uuid(),
        playerInfo.name()));
    apiClient.sendPlayerLeave(LeaveEventRequest.from(playerInfo, occurredAt()));
  }

  private long occurredAt() {
    return Instant.now().getEpochSecond();
  }
}
