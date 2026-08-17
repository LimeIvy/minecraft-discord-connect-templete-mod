package com.example.discordconnector;

import com.example.discordconnector.api.ApiClient;
import com.example.discordconnector.config.ForgeApiConfig;
import com.example.discordconnector.config.ForgeConfig;
import com.example.discordconnector.event.HeartbeatEventHandler;
import com.example.discordconnector.event.PlayerEventHandler;
import com.example.discordconnector.event.ServerEventHandler;
import com.example.discordconnector.service.CommunityService;
import com.example.discordconnector.service.HeartbeatService;
import com.example.discordconnector.service.LinkService;
import com.example.discordconnector.service.ServerLifecycleService;
import com.example.discordconnector.logging.CommonLogger;
import com.example.discordconnector.logging.ForgeCommonLogger;
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

  private final LinkService linkService;

  public DiscordConnectorForge() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ForgeConfig.SPEC);

    CommonLogger commonLogger = new ForgeCommonLogger();
    ApiClient apiClient = new ApiClient(new ForgeApiConfig(), commonLogger);
    HeartbeatService heartbeatService = new HeartbeatService(apiClient);
    linkService = new LinkService(apiClient);

    MinecraftForge.EVENT_BUS.register(this);
    MinecraftForge.EVENT_BUS.register(new PlayerEventHandler(
        new CommunityService(apiClient, commonLogger)));
    MinecraftForge.EVENT_BUS.register(new ServerEventHandler(
        new ServerLifecycleService(apiClient, heartbeatService)));
    MinecraftForge.EVENT_BUS.register(new HeartbeatEventHandler(heartbeatService));
  }

  @SubscribeEvent
  public void onCommandsRegister(RegisterCommandsEvent event) {
    DiscordCommand.register(event.getDispatcher(), linkService);
  }
}
