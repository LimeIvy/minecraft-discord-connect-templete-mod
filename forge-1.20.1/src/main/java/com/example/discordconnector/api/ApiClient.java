package com.example.discordconnector.api;

import com.example.discordconnector.DiscordConnectorForge;
import com.example.discordconnector.config.ForgeConfig;
import com.example.discordconnector.model.JoinEventRequest;
import com.example.discordconnector.model.LeaveEventRequest;
import com.example.discordconnector.model.ServerEventRequest;
import com.example.discordconnector.util.JsonUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ApiClient {
  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

  private final HttpClient httpClient;

  public ApiClient() {
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(REQUEST_TIMEOUT)
        .build();
  }

  public void sendPlayerJoin(JoinEventRequest request) {
    sendPost("/v1/minecraft/events/join", request.serverId(), JsonUtil.toJson(request));
  }

  public void sendPlayerLeave(LeaveEventRequest request) {
    sendPost("/v1/minecraft/events/leave", request.serverId(), JsonUtil.toJson(request));
  }

  public void sendServerStart(ServerEventRequest request) {
    sendPost(
        "/v1/minecraft/events/server-start",
        request.serverId(),
        JsonUtil.toJson(request));
  }

  public void sendServerStop(ServerEventRequest request) {
    sendPost(
        "/v1/minecraft/events/server-stop",
        request.serverId(),
        JsonUtil.toJson(request));
  }

  private void sendPost(String path, String serverId, String requestBody) {
    DiscordConnectorForge.LOGGER.info(
        "Prepared API request: method=POST, path={}, server_id={}, api_url={}, api_key_configured={}, body={}",
        path,
        serverId,
        ForgeConfig.apiUrl(),
        ForgeConfig.hasApiKey(),
        requestBody);

    if (!ForgeConfig.hasApiKey()) {
      DiscordConnectorForge.LOGGER.warn(
          "Skipped API request because api_key is not configured: path={}, server_id={}",
          path,
          serverId);
      return;
    }

    URI uri = resolveUri(path);
    if (uri == null) {
      DiscordConnectorForge.LOGGER.warn(
          "Skipped API request because api_url is invalid: path={}, server_id={}, api_url={}",
          path,
          serverId,
          ForgeConfig.apiUrl());
      return;
    }

    HttpRequest request = HttpRequest.newBuilder()
        .uri(uri)
        .timeout(REQUEST_TIMEOUT)
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + ForgeConfig.apiKey())
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .build();

    httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
        .whenComplete((response, throwable) -> {
          if (throwable != null) {
            DiscordConnectorForge.LOGGER.warn(
                "API request failed: path={}, server_id={}, error={}",
                path,
                serverId,
                throwable.toString());
            return;
          }
          logResponse(path, serverId, response.statusCode());
        });
  }

  private void logResponse(String path, String serverId, int statusCode) {
    if (statusCode >= 200 && statusCode < 300) {
      DiscordConnectorForge.LOGGER.info(
          "API request completed: path={}, server_id={}, status={}",
          path,
          serverId,
          statusCode);
      return;
    }

    DiscordConnectorForge.LOGGER.warn(
        "API request returned non-success status: path={}, server_id={}, status={}",
        path,
        serverId,
        statusCode);
  }

  private URI resolveUri(String path) {
    String apiUrl = ForgeConfig.apiUrl();
    String separator = apiUrl.endsWith("/") ? "" : "/";
    String normalizedPath = path.startsWith("/") ? path.substring(1) : path;
    try {
      URI uri = new URI(apiUrl + separator + normalizedPath);
      if (!"https".equalsIgnoreCase(uri.getScheme())) {
        return null;
      }
      return uri;
    } catch (URISyntaxException exception) {
      return null;
    }
  }
}
