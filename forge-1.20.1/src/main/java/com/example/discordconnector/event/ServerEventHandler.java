package com.example.discordconnector.event;

import com.example.discordconnector.DiscordConnectorForge;
import com.example.discordconnector.config.ForgeConfig;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerEventHandler {
  @SubscribeEvent
  public void onServerStarted(ServerStartedEvent event) {
    DiscordConnectorForge.LOGGER.info(
        "Server started: server_id={}, api_url={}, api_key_configured={}",
        ForgeConfig.serverId(),
        ForgeConfig.apiUrl(),
        ForgeConfig.hasApiKey());
  }

  @SubscribeEvent
  public void onServerStopping(ServerStoppingEvent event) {
    DiscordConnectorForge.LOGGER.info("Server stopping: server_id={}", ForgeConfig.serverId());
  }
}
