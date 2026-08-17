package com.example.discordconnector.logging;

import com.example.discordconnector.DiscordConnectorForge;

public class ForgeCommonLogger implements CommonLogger {
  @Override
  public void debug(String message) {
    DiscordConnectorForge.LOGGER.debug(message);
  }

  @Override
  public void info(String message) {
    DiscordConnectorForge.LOGGER.info(message);
  }

  @Override
  public void warn(String message) {
    DiscordConnectorForge.LOGGER.warn(message);
  }
}
