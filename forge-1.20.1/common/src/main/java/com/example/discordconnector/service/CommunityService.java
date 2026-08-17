package com.example.discordconnector.service;

import com.example.discordconnector.api.ApiClient;
import com.example.discordconnector.logging.CommonLogger;
import com.example.discordconnector.model.JoinEventRequest;
import com.example.discordconnector.model.LeaveEventRequest;
import com.example.discordconnector.model.PlayerInfo;
import java.time.Instant;

public class CommunityService {
  private final ApiClient apiClient;
  private final CommonLogger logger;

  public CommunityService(ApiClient apiClient, CommonLogger logger) {
    this.apiClient = apiClient;
    this.logger = logger;
  }

  public void onPlayerJoin(PlayerInfo playerInfo) {
    logger.info(String.format(
        "Player joined: server_id=%s, uuid=%s, name=%s",
        playerInfo.serverId(),
        playerInfo.uuid(),
        playerInfo.name()));
    apiClient.sendPlayerJoin(JoinEventRequest.from(playerInfo, occurredAt()));
  }

  public void onPlayerLeave(PlayerInfo playerInfo) {
    logger.info(String.format(
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
