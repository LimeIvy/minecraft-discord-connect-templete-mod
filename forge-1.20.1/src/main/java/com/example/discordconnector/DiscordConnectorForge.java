package com.example.discordconnector;

import com.example.discordconnector.config.ForgeConfig;
import com.example.discordconnector.event.PlayerEventHandler;
import com.example.discordconnector.event.ServerEventHandler;
import com.example.discordconnector.service.CommunityService;
import com.example.discordconnector.service.ServerLifecycleService;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(DiscordConnectorForge.MODID)
public class DiscordConnectorForge {
  public static final String MODID = "discord_connector";
  public static final Logger LOGGER = LogUtils.getLogger();

  public DiscordConnectorForge() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ForgeConfig.SPEC);

    MinecraftForge.EVENT_BUS.register(this);
    MinecraftForge.EVENT_BUS.register(new PlayerEventHandler(new CommunityService()));
    MinecraftForge.EVENT_BUS.register(new ServerEventHandler(new ServerLifecycleService()));
  }

  @SubscribeEvent
  public void onCommandsRegister(RegisterCommandsEvent event) {
    DiscordCommand.register(event.getDispatcher());
  }
}
