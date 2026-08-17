package com.example.discordconnector.event;

import com.example.discordconnector.DiscordConnectorForge;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerEventHandler {
  @SubscribeEvent
  public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    if (event.getEntity() instanceof ServerPlayer player) {
      DiscordConnectorForge.LOGGER.info(
          "Player joined: uuid={}, name={}",
          player.getUUID(),
          player.getGameProfile().getName());
    }
  }

  @SubscribeEvent
  public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
    if (event.getEntity() instanceof ServerPlayer player) {
      DiscordConnectorForge.LOGGER.info(
          "Player left: uuid={}, name={}",
          player.getUUID(),
          player.getGameProfile().getName());
    }
  }
}
