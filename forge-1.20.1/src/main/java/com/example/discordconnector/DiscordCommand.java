package com.example.discordconnector;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class DiscordCommand {
  public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    dispatcher.register(Commands.literal("discord")
        .executes(context -> {
          // チャットにメッセージを表示
          context.getSource().sendSuccess(() -> Component.literal("discord-connector"), false);
          return 1;
        }));
  }
}
