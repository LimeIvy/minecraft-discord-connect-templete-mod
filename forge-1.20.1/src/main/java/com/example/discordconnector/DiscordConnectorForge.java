package com.example.discordconnector;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(DiscordConnectorForge.MODID)
public class DiscordConnectorForge {
  public static final String MODID = "discord_connector";

  public DiscordConnectorForge() {
    MinecraftForge.EVENT_BUS.register(this);
  }

  // コマンド登録イベント
  @SubscribeEvent
  public void onCommandsRegister(RegisterCommandsEvent event) {
    DiscordCommand.register(event.getDispatcher());
  }
}
