package com.example.discordconnector.event;

import com.example.discordconnector.config.ForgeConfig;
import com.example.discordconnector.model.PlayerInfo;
import com.example.discordconnector.service.CommunityService;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerEventHandler {
  private final CommunityService communityService;

  public PlayerEventHandler(CommunityService communityService) {
    this.communityService = communityService;
  }

  @SubscribeEvent
  public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    if (event.getEntity() instanceof ServerPlayer player) {
      communityService.onPlayerJoin(toPlayerInfo(player));
    }
  }

  @SubscribeEvent
  public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
    if (event.getEntity() instanceof ServerPlayer player) {
      communityService.onPlayerLeave(toPlayerInfo(player));
    }
  }

  private PlayerInfo toPlayerInfo(ServerPlayer player) {
    return new PlayerInfo(
        player.getUUID(),
        player.getGameProfile().getName(),
        ForgeConfig.serverId());
  }
}
