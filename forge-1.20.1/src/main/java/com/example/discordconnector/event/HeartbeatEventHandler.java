package com.example.discordconnector.event;

import com.example.discordconnector.service.HeartbeatService;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HeartbeatEventHandler {
  private final HeartbeatService heartbeatService;

  public HeartbeatEventHandler(HeartbeatService heartbeatService) {
    this.heartbeatService = heartbeatService;
  }

  @SubscribeEvent
  public void onServerTick(TickEvent.ServerTickEvent event) {
    if (event.phase != TickEvent.Phase.END) {
      return;
    }

    heartbeatService.tick(event.getServer());
  }
}
