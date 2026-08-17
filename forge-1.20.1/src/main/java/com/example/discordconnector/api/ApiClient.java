package com.example.discordconnector.api;

import com.example.discordconnector.DiscordConnectorForge;
import com.example.discordconnector.config.ForgeConfig;
import com.example.discordconnector.model.JoinEventRequest;
import com.example.discordconnector.model.LeaveEventRequest;
import com.example.discordconnector.model.ServerEventRequest;

public class ApiClient {
  public void sendPlayerJoin(JoinEventRequest request) {
    logPreparedRequest("/v1/minecraft/events/join", request.serverId());
  }

  public void sendPlayerLeave(LeaveEventRequest request) {
    logPreparedRequest("/v1/minecraft/events/leave", request.serverId());
  }

  public void sendServerStart(ServerEventRequest request) {
    logPreparedRequest("/v1/minecraft/events/server-start", request.serverId());
  }

  public void sendServerStop(ServerEventRequest request) {
    logPreparedRequest("/v1/minecraft/events/server-stop", request.serverId());
  }

  private void logPreparedRequest(String path, String serverId) {
    DiscordConnectorForge.LOGGER.info(
        "Prepared API request: method=POST, path={}, server_id={}, api_url={}, api_key_configured={}",
        path,
        serverId,
        ForgeConfig.apiUrl(),
        ForgeConfig.hasApiKey());
  }
}
