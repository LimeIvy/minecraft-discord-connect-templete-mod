package com.example.discordconnector.service;

import com.example.discordconnector.DiscordConnectorForge;
import com.example.discordconnector.api.ApiClient;
import com.example.discordconnector.config.ForgeConfig;
import com.example.discordconnector.model.ServerEventRequest;

public class ServerLifecycleService {
  private final ApiClient apiClient;

  public ServerLifecycleService(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public void onServerStarted() {
    DiscordConnectorForge.LOGGER.info(
        "Server started: server_id={}, api_url={}, api_key_configured={}",
        ForgeConfig.serverId(),
        ForgeConfig.apiUrl(),
        ForgeConfig.hasApiKey());
    apiClient.sendServerStart(new ServerEventRequest(ForgeConfig.serverId()));
  }

  public void onServerStopping() {
    DiscordConnectorForge.LOGGER.info("Server stopping: server_id={}", ForgeConfig.serverId());
    apiClient.sendServerStop(new ServerEventRequest(ForgeConfig.serverId()));
  }
}
