package com.example.discordconnector.api;

public interface ApiConfig {
  String apiUrl();

  String apiKey();

  default boolean hasApiUrl() {
    return !apiUrl().isBlank();
  }

  default boolean hasApiKey() {
    return !apiKey().isBlank();
  }
}
