package com.example.discordconnector.event;

import com.example.discordconnector.service.ServerLifecycleService;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerEventHandler {
  private final ServerLifecycleService serverLifecycleService;

  public ServerEventHandler(ServerLifecycleService serverLifecycleService) {
    this.serverLifecycleService = serverLifecycleService;
  }

  @SubscribeEvent
  public void onServerStarted(ServerStartedEvent event) {
    serverLifecycleService.onServerStarted();
  }

  @SubscribeEvent
  public void onServerStopping(ServerStoppingEvent event) {
    serverLifecycleService.onServerStopping();
  }
}
