package com.example.discordconnector;

import com.example.discordconnector.config.ForgeConfig;
import com.example.discordconnector.model.LinkCodeResponse;
import com.example.discordconnector.model.PlayerInfo;
import com.example.discordconnector.service.LinkService;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class DiscordCommand {
  public static void register(
      CommandDispatcher<CommandSourceStack> dispatcher,
      LinkService linkService) {
    dispatcher.register(Commands.literal("discord")
        .then(Commands.literal("link")
            .executes(context -> link(context.getSource(), linkService)))
        .executes(context -> {
          context.getSource().sendSuccess(
              () -> Component.translatable("discord_connector.command.root"), false);
          return 1;
        }));
  }

  private static int link(CommandSourceStack source, LinkService linkService)
      throws CommandSyntaxException {
    ServerPlayer player = source.getPlayerOrException();
    PlayerInfo playerInfo = new PlayerInfo(
        player.getUUID(),
        player.getGameProfile().getName(),
        ForgeConfig.serverId());

    source.sendSuccess(
        () -> Component.translatable("discord_connector.link.requesting"), false);

    linkService.issueLinkCode(playerInfo)
        .whenComplete((response, throwable) -> player.getServer().execute(() -> {
          if (throwable != null) {
            DiscordConnectorForge.LOGGER.warn(
                "Failed to issue Discord link code: server_id={}, uuid={}, error={}",
                playerInfo.serverId(),
                playerInfo.uuid(),
                throwable.toString());
            player.sendSystemMessage(
                Component.translatable("discord_connector.link.failed"));
            return;
          }

          player.sendSystemMessage(message(response));
        }));

    return 1;
  }

  private static Component message(LinkCodeResponse response) {
    return Component.translatable(
        "discord_connector.link.success",
        response.code(),
        response.code());
  }
}
