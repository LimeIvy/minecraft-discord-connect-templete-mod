package com.example.discordconnector.event;

import com.example.discordconnector.DiscordConnectorForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerEventHandler {
  @SubscribeEvent
  public void onServerStarted(ServerStartedEvent event) {
    DiscordConnectorForge.LOGGER.info("Server started");
  }

  @SubscribeEvent
  public void onServerStopping(ServerStoppingEvent event) {
    DiscordConnectorForge.LOGGER.info("Server stopping");
  }
}
