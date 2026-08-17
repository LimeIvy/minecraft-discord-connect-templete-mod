package com.example.discordconnector.api;

import com.example.discordconnector.DiscordConnectorForge;
import com.example.discordconnector.config.ForgeConfig;
import com.example.discordconnector.model.HeartbeatRequest;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ApiClient implements AutoCloseable {
  private static final int MAX_ATTEMPTS = 3;
  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

  private final ExecutorService executorService;
  private final HttpClient httpClient;

  public ApiClient() {
    this.executorService = Executors.newSingleThreadExecutor(new ApiThreadFactory());
    this.httpClient = HttpClient.newBuilder()
        .connectTimeout(REQUEST_TIMEOUT)
        .executor(executorService)
        .build();
  }

  @Override
  public void close() {
    executorService.shutdown();
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

  public void sendHeartbeat(HeartbeatRequest request) {
    sendPost(
        "/v1/minecraft/heartbeat",
        request.serverId(),
        JsonUtil.toJson(request));
  }

  private void sendPost(String path, String serverId, String requestBody) {
    DiscordConnectorForge.LOGGER.debug(
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

    sendWithRetry(request, path, serverId, 1);
  }

  private void sendWithRetry(HttpRequest request, String path, String serverId, int attempt) {
    httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
        .whenCompleteAsync((response, throwable) -> {
          if (throwable != null) {
            handleFailedAttempt(request, path, serverId, attempt, throwable);
            return;
          }
          handleResponse(request, path, serverId, attempt, response.statusCode());
        }, executorService);
  }

  private void handleFailedAttempt(
      HttpRequest request,
      String path,
      String serverId,
      int attempt,
      Throwable throwable) {
    if (attempt < MAX_ATTEMPTS) {
      DiscordConnectorForge.LOGGER.warn(
          "API request failed; retrying: path={}, server_id={}, attempt={}, max_attempts={}, error={}",
          path,
          serverId,
          attempt,
          MAX_ATTEMPTS,
          throwable.toString());
      sendWithRetry(request, path, serverId, attempt + 1);
      return;
    }

    DiscordConnectorForge.LOGGER.warn(
        "API request failed: path={}, server_id={}, attempts={}, error={}",
        path,
        serverId,
        attempt,
        throwable.toString());
  }

  private void handleResponse(
      HttpRequest request,
      String path,
      String serverId,
      int attempt,
      int statusCode) {
    if (shouldRetry(statusCode) && attempt < MAX_ATTEMPTS) {
      DiscordConnectorForge.LOGGER.warn(
          "API request returned retryable status; retrying: path={}, server_id={}, status={}, attempt={}, max_attempts={}",
          path,
          serverId,
          statusCode,
          attempt,
          MAX_ATTEMPTS);
      sendWithRetry(request, path, serverId, attempt + 1);
      return;
    }

    logResponse(path, serverId, statusCode, attempt);
  }

  private void logResponse(String path, String serverId, int statusCode, int attempts) {
    if (statusCode >= 200 && statusCode < 300) {
      DiscordConnectorForge.LOGGER.info(
          "API request completed: path={}, server_id={}, status={}, attempts={}",
          path,
          serverId,
          statusCode,
          attempts);
      return;
    }

    DiscordConnectorForge.LOGGER.warn(
        "API request returned non-success status: path={}, server_id={}, status={}, attempts={}",
        path,
        serverId,
        statusCode,
        attempts);
  }

  private boolean shouldRetry(int statusCode) {
    return statusCode == 429 || statusCode >= 500;
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

  private static final class ApiThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable runnable) {
      Thread thread = new Thread(runnable, "discord-connector-api");
      thread.setDaemon(true);
      return thread;
    }
  }
}
