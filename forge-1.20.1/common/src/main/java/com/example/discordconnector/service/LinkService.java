package com.example.discordconnector.service;

import com.example.discordconnector.api.ApiClient;
import com.example.discordconnector.model.LinkCodeRequest;
import com.example.discordconnector.model.LinkCodeResponse;
import com.example.discordconnector.model.PlayerInfo;
import java.util.concurrent.CompletableFuture;

public class LinkService {
  private final ApiClient apiClient;

  public LinkService(ApiClient apiClient) {
    this.apiClient = apiClient;
  }

  public CompletableFuture<LinkCodeResponse> issueLinkCode(PlayerInfo playerInfo) {
    return apiClient.requestLinkCode(LinkCodeRequest.from(playerInfo));
  }
}
