package com.example.discordconnector.api;

public interface ApiConfig {
  String apiUrl();

  String apiKey();

  default boolean hasApiKey() {
    return !apiKey().isBlank();
  }
}
