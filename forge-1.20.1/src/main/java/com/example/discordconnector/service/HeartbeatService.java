package com.example.discordconnector.service;

import com.example.discordconnector.api.ApiClient;
import com.example.discordconnector.config.ForgeConfig;
import com.example.discordconnector.model.HeartbeatRequest;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import net.minecraft.server.MinecraftServer;

public class HeartbeatService {
  private static final Duration HEARTBEAT_INTERVAL = Duration.ofMinutes(5);

  private final ApiClient apiClient;
  private Instant nextHeartbeatAt = Instant.MAX;

  public HeartbeatService(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public void start() {
    nextHeartbeatAt = Instant.now().plus(HEARTBEAT_INTERVAL);
  }

  public void stop() {
    nextHeartbeatAt = Instant.MAX;
  }

  public void tick(MinecraftServer server) {
    Instant now = Instant.now();
    if (now.isBefore(nextHeartbeatAt)) {
      return;
    }

    nextHeartbeatAt = now.plus(HEARTBEAT_INTERVAL);
    apiClient.sendHeartbeat(new HeartbeatRequest(
        ForgeConfig.serverId(),
        onlinePlayerIds(server),
        now.getEpochSecond()));
  }

  private List<String> onlinePlayerIds(MinecraftServer server) {
    return server.getPlayerList().getPlayers().stream()
        .map(player -> player.getUUID().toString())
        .toList();
  }
}
