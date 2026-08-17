package com.example.discordconnector.config;

import com.example.discordconnector.api.ApiConfig;

public class ForgeApiConfig implements ApiConfig {
  @Override
  public String apiUrl() {
    return ForgeConfig.apiUrl();
  }

  @Override
  public String apiKey() {
    return ForgeConfig.apiKey();
  }
}
