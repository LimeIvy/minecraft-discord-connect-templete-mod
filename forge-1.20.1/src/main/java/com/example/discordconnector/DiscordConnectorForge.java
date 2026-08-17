package com.example.discordconnector;

import com.example.discordconnector.event.PlayerEventHandler;
import com.example.discordconnector.event.ServerEventHandler;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(DiscordConnectorForge.MODID)
public class DiscordConnectorForge {
  public static final String MODID = "discord_connector";
  public static final Logger LOGGER = LogUtils.getLogger();

  public DiscordConnectorForge() {
    MinecraftForge.EVENT_BUS.register(this);
    MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());
    MinecraftForge.EVENT_BUS.register(new ServerEventHandler());
  }

  @SubscribeEvent
  public void onCommandsRegister(RegisterCommandsEvent event) {
    DiscordCommand.register(event.getDispatcher());
  }
}
