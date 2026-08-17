package com.example.discordconnector;

import com.example.discordconnector.config.ForgeConfig;
import com.example.discordconnector.model.LinkCodeResponse;
import com.example.discordconnector.model.PlayerInfo;
import com.example.discordconnector.service.LinkService;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.CommandDispatcher;
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
          context.getSource().sendSuccess(() -> Component.literal("discord-connector"), false);
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

    source.sendSuccess(() -> Component.literal("Discord連携コードを発行しています..."), false);

    linkService.issueLinkCode(playerInfo)
        .whenComplete((response, throwable) -> player.getServer().execute(() -> {
          if (throwable != null) {
            DiscordConnectorForge.LOGGER.warn(
                "Failed to issue Discord link code: server_id={}, uuid={}, error={}",
                playerInfo.serverId(),
                playerInfo.uuid(),
                throwable.toString());
            player.sendSystemMessage(Component.literal(
                "Discord連携コードの発行に失敗しました。時間をおいて再度お試しください。"));
            return;
          }

          player.sendSystemMessage(Component.literal(message(response)));
        }));

    return 1;
  }

  private static String message(LinkCodeResponse response) {
    return "Discord連携コードを発行しました。\n"
        + response.code()
        + "\n\nDiscordで\n/link code:"
        + response.code()
        + "\nを実行してください。\nこのコードは10分間有効です。";
  }
}
