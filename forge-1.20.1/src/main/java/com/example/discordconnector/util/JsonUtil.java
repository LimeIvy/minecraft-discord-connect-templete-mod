package com.example.discordconnector.util;

import com.example.discordconnector.model.JoinEventRequest;
import com.example.discordconnector.model.LeaveEventRequest;
import com.example.discordconnector.model.ServerEventRequest;

public final class JsonUtil {
  private JsonUtil() {
  }

  public static String toJson(JoinEventRequest request) {
    return "{"
        + field("uuid", request.uuid().toString()) + ","
        + field("name", request.name()) + ","
        + field("serverId", request.serverId())
        + "}";
  }

  public static String toJson(LeaveEventRequest request) {
    return "{"
        + field("uuid", request.uuid().toString()) + ","
        + field("name", request.name()) + ","
        + field("serverId", request.serverId())
        + "}";
  }

  public static String toJson(ServerEventRequest request) {
    return "{"
        + field("serverId", request.serverId())
        + "}";
  }

  private static String field(String name, String value) {
    return quote(name) + ":" + quote(value);
  }

  private static String quote(String value) {
    return "\"" + escape(value) + "\"";
  }

  private static String escape(String value) {
    StringBuilder builder = new StringBuilder(value.length());
    for (int index = 0; index < value.length(); index++) {
      char character = value.charAt(index);
      switch (character) {
        case '"' -> builder.append("\\\"");
        case '\\' -> builder.append("\\\\");
        case '\b' -> builder.append("\\b");
        case '\f' -> builder.append("\\f");
        case '\n' -> builder.append("\\n");
        case '\r' -> builder.append("\\r");
        case '\t' -> builder.append("\\t");
        default -> appendEscapedCharacter(builder, character);
      }
    }
    return builder.toString();
  }

  private static void appendEscapedCharacter(StringBuilder builder, char character) {
    if (character < 0x20) {
      builder.append(String.format("\\u%04x", (int) character));
      return;
    }
    builder.append(character);
  }
}
