package com.example.discordconnector.service;

import com.example.discordconnector.DiscordConnectorForge;
import com.example.discordconnector.api.ApiClient;
import com.example.discordconnector.config.ForgeConfig;
import com.example.discordconnector.model.ServerEventRequest;
import java.time.Instant;

public class ServerLifecycleService {
  private final ApiClient apiClient;
  private final HeartbeatService heartbeatService;

  public ServerLifecycleService(ApiClient apiClient, HeartbeatService heartbeatService) {
    this.apiClient = apiClient;
    this.heartbeatService = heartbeatService;
  }

  public void onServerStarted() {
    DiscordConnectorForge.LOGGER.info(
        "Server started: server_id={}, api_url={}, api_key_configured={}",
        ForgeConfig.serverId(),
        ForgeConfig.apiUrl(),
        ForgeConfig.hasApiKey());
    apiClient.sendServerStart(new ServerEventRequest(ForgeConfig.serverId(), occurredAt()));
    heartbeatService.start();
  }

  public void onServerStopping() {
    DiscordConnectorForge.LOGGER.info("Server stopping: server_id={}", ForgeConfig.serverId());
    heartbeatService.stop();
    apiClient.sendServerStop(new ServerEventRequest(ForgeConfig.serverId(), occurredAt()));
  }

  private long occurredAt() {
    return Instant.now().getEpochSecond();
  }
}
