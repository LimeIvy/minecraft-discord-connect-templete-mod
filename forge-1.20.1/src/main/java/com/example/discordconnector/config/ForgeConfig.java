package com.example.discordconnector.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ForgeConfig {
  public static final ForgeConfigSpec SPEC;

  private static final ForgeConfigSpec.ConfigValue<String> SERVER_ID;
  private static final ForgeConfigSpec.ConfigValue<String> API_URL;
  private static final ForgeConfigSpec.ConfigValue<String> API_KEY;

  static {
    ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

    builder.push("discord_connector");

    SERVER_ID = builder
        .comment("Unique server identifier used by the Discord Connector API.")
        .define("server_id", "template-server");

    API_URL = builder
        .comment("Base URL of your own Discord Connector API. Leave blank to disable API calls.")
        .define("api_url", "");

    API_KEY = builder
        .comment("Bearer token for your own Discord Connector API. Do not commit real keys.")
        .define("api_key", "");

    builder.pop();

    SPEC = builder.build();
  }

  private ForgeConfig() {
  }

  public static String serverId() {
    return SERVER_ID.get();
  }

  public static String apiUrl() {
    return API_URL.get();
  }

  public static String apiKey() {
    return API_KEY.get();
  }

  public static boolean hasApiKey() {
    return !apiKey().isBlank();
  }
}
