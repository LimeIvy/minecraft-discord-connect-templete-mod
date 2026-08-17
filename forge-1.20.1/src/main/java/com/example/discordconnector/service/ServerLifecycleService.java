package com.example.discordconnector.service;

import com.example.discordconnector.DiscordConnectorForge;
import com.example.discordconnector.config.ForgeConfig;

public class ServerLifecycleService {
  public void onServerStarted() {
    DiscordConnectorForge.LOGGER.info(
        "Server started: server_id={}, api_url={}, api_key_configured={}",
        ForgeConfig.serverId(),
        ForgeConfig.apiUrl(),
        ForgeConfig.hasApiKey());
  }

  public void onServerStopping() {
    DiscordConnectorForge.LOGGER.info("Server stopping: server_id={}", ForgeConfig.serverId());
  }
}
