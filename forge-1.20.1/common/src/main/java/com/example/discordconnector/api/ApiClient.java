package com.example.discordconnector.api;

import com.example.discordconnector.model.HeartbeatRequest;
import com.example.discordconnector.model.JoinEventRequest;
import com.example.discordconnector.model.LeaveEventRequest;
import com.example.discordconnector.model.LinkCodeRequest;
import com.example.discordconnector.model.LinkCodeResponse;
import com.example.discordconnector.model.ServerEventRequest;
import com.example.discordconnector.logging.CommonLogger;
import com.example.discordconnector.util.JsonUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ApiClient implements AutoCloseable {
  private static final int MAX_ATTEMPTS = 3;
  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(5);

  private final ExecutorService executorService;
  private final HttpClient httpClient;
  private final ApiConfig apiConfig;
  private final CommonLogger logger;

  public ApiClient(ApiConfig apiConfig, CommonLogger logger) {
    this.apiConfig = apiConfig;
    this.logger = logger;
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

  public CompletableFuture<LinkCodeResponse> requestLinkCode(LinkCodeRequest request) {
    return sendPostForString(
        "/v1/minecraft/link-code",
        request.serverId(),
        JsonUtil.toJson(request))
        .thenApply(this::parseLinkCodeResponse);
  }

  private void sendPost(String path, String serverId, String requestBody) {
    logger.debug(String.format(
        "Prepared API request: method=POST, path=%s, server_id=%s, api_url=%s, api_key_configured=%s, body=%s",
        path,
        serverId,
        apiConfig.apiUrl(),
        apiConfig.hasApiKey(),
        requestBody));

    if (!apiConfig.hasApiUrl()) {
      logger.warn(String.format(
          "Skipped API request because api_url is not configured: path=%s, server_id=%s",
          path,
          serverId));
      return;
    }

    if (!apiConfig.hasApiKey()) {
      logger.warn(String.format(
          "Skipped API request because api_key is not configured: path=%s, server_id=%s",
          path,
          serverId));
      return;
    }

    URI uri = resolveUri(path);
    if (uri == null) {
      logger.warn(String.format(
          "Skipped API request because api_url is invalid: path=%s, server_id=%s, api_url=%s",
          path,
          serverId,
          apiConfig.apiUrl()));
      return;
    }

    HttpRequest request = HttpRequest.newBuilder()
        .uri(uri)
        .timeout(REQUEST_TIMEOUT)
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + apiConfig.apiKey())
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .build();

    sendWithRetry(request, path, serverId, 1);
  }

  private CompletableFuture<String> sendPostForString(String path, String serverId, String requestBody) {
    logger.debug(String.format(
        "Prepared API request: method=POST, path=%s, server_id=%s, api_url=%s, api_key_configured=%s, body=%s",
        path,
        serverId,
        apiConfig.apiUrl(),
        apiConfig.hasApiKey(),
        requestBody));

    if (!apiConfig.hasApiUrl()) {
      return CompletableFuture.failedFuture(
          new IllegalStateException("api_url is not configured"));
    }

    if (!apiConfig.hasApiKey()) {
      return CompletableFuture.failedFuture(
          new IllegalStateException("api_key is not configured"));
    }

    URI uri = resolveUri(path);
    if (uri == null) {
      return CompletableFuture.failedFuture(
          new IllegalStateException("api_url is invalid: " + apiConfig.apiUrl()));
    }

    HttpRequest request = HttpRequest.newBuilder()
        .uri(uri)
        .timeout(REQUEST_TIMEOUT)
        .header("Content-Type", "application/json")
        .header("Authorization", "Bearer " + apiConfig.apiKey())
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .build();

    CompletableFuture<String> result = new CompletableFuture<>();
    sendWithRetryForString(request, path, serverId, 1, result);
    return result;
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

  private void sendWithRetryForString(
      HttpRequest request,
      String path,
      String serverId,
      int attempt,
      CompletableFuture<String> result) {
    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .whenCompleteAsync((response, throwable) -> {
          if (throwable != null) {
            if (attempt < MAX_ATTEMPTS) {
              logger.warn(String.format(
                  "API request failed; retrying: path=%s, server_id=%s, attempt=%d, max_attempts=%d, error=%s",
                  path,
                  serverId,
                  attempt,
                  MAX_ATTEMPTS,
                  throwable));
              sendWithRetryForString(request, path, serverId, attempt + 1, result);
              return;
            }
            result.completeExceptionally(throwable);
            return;
          }

          int statusCode = response.statusCode();
          if (shouldRetry(statusCode) && attempt < MAX_ATTEMPTS) {
            logger.warn(String.format(
                "API request returned retryable status; retrying: path=%s, server_id=%s, status=%d, attempt=%d, max_attempts=%d",
                path,
                serverId,
                statusCode,
                attempt,
                MAX_ATTEMPTS));
            sendWithRetryForString(request, path, serverId, attempt + 1, result);
            return;
          }

          if (statusCode < 200 || statusCode >= 300) {
            result.completeExceptionally(new IllegalStateException(
                "API request returned status " + statusCode));
            return;
          }

          logger.info(String.format(
              "API request completed: path=%s, server_id=%s, status=%d, attempts=%d",
              path,
              serverId,
              statusCode,
              attempt));
          result.complete(response.body());
        }, executorService);
  }

  private void handleFailedAttempt(
      HttpRequest request,
      String path,
      String serverId,
      int attempt,
      Throwable throwable) {
    if (attempt < MAX_ATTEMPTS) {
      logger.warn(String.format(
          "API request failed; retrying: path=%s, server_id=%s, attempt=%d, max_attempts=%d, error=%s",
          path,
          serverId,
          attempt,
          MAX_ATTEMPTS,
          throwable));
      sendWithRetry(request, path, serverId, attempt + 1);
      return;
    }

    logger.warn(String.format(
        "API request failed: path=%s, server_id=%s, attempts=%d, error=%s",
        path,
        serverId,
        attempt,
        throwable));
  }

  private void handleResponse(
      HttpRequest request,
      String path,
      String serverId,
      int attempt,
      int statusCode) {
    if (shouldRetry(statusCode) && attempt < MAX_ATTEMPTS) {
      logger.warn(String.format(
          "API request returned retryable status; retrying: path=%s, server_id=%s, status=%d, attempt=%d, max_attempts=%d",
          path,
          serverId,
          statusCode,
          attempt,
          MAX_ATTEMPTS));
      sendWithRetry(request, path, serverId, attempt + 1);
      return;
    }

    logResponse(path, serverId, statusCode, attempt);
  }

  private void logResponse(String path, String serverId, int statusCode, int attempts) {
    if (statusCode >= 200 && statusCode < 300) {
      logger.info(String.format(
          "API request completed: path=%s, server_id=%s, status=%d, attempts=%d",
          path,
          serverId,
          statusCode,
          attempts));
      return;
    }

    logger.warn(String.format(
        "API request returned non-success status: path=%s, server_id=%s, status=%d, attempts=%d",
        path,
        serverId,
        statusCode,
        attempts));
  }

  private boolean shouldRetry(int statusCode) {
    return statusCode == 429 || statusCode >= 500;
  }

  private LinkCodeResponse parseLinkCodeResponse(String responseBody) {
    String code = findStringValue(responseBody, "code")
        .orElseThrow(() -> new IllegalStateException("link code was not found in API response"));
    long expiresAt = findLongValue(responseBody, "expiresAt")
        .orElseThrow(() -> new IllegalStateException("link code expiry was not found in API response"));
    return new LinkCodeResponse(code, expiresAt);
  }

  private Optional<String> findStringValue(String json, String fieldName) {
    String pattern = "\"" + fieldName + "\"";
    int fieldIndex = json.indexOf(pattern);
    if (fieldIndex < 0) {
      return Optional.empty();
    }

    int colonIndex = json.indexOf(':', fieldIndex + pattern.length());
    if (colonIndex < 0) {
      return Optional.empty();
    }

    int valueStart = json.indexOf('"', colonIndex + 1);
    if (valueStart < 0) {
      return Optional.empty();
    }

    StringBuilder value = new StringBuilder();
    boolean escaped = false;
    for (int index = valueStart + 1; index < json.length(); index++) {
      char character = json.charAt(index);
      if (escaped) {
        value.append(character);
        escaped = false;
        continue;
      }
      if (character == '\\') {
        escaped = true;
        continue;
      }
      if (character == '"') {
        return Optional.of(value.toString());
      }
      value.append(character);
    }
    return Optional.empty();
  }

  private Optional<Integer> findIntValue(String json, String fieldName) {
    return findLongValue(json, fieldName).map(Long::intValue);
  }

  private Optional<Long> findLongValue(String json, String fieldName) {
    String pattern = "\"" + fieldName + "\"";
    int fieldIndex = json.indexOf(pattern);
    if (fieldIndex < 0) {
      return Optional.empty();
    }

    int colonIndex = json.indexOf(':', fieldIndex + pattern.length());
    if (colonIndex < 0) {
      return Optional.empty();
    }

    int valueStart = colonIndex + 1;
    while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
      valueStart++;
    }

    int valueEnd = valueStart;
    while (valueEnd < json.length() && Character.isDigit(json.charAt(valueEnd))) {
      valueEnd++;
    }

    if (valueEnd == valueStart) {
      return Optional.empty();
    }
    return Optional.of(Long.parseLong(json.substring(valueStart, valueEnd)));
  }

  private URI resolveUri(String path) {
    String apiUrl = apiConfig.apiUrl();
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
