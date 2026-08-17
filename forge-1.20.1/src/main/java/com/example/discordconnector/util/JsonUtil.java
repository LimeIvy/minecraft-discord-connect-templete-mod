package com.example.discordconnector.util;

import com.example.discordconnector.model.HeartbeatRequest;
import com.example.discordconnector.model.JoinEventRequest;
import com.example.discordconnector.model.LeaveEventRequest;
import com.example.discordconnector.model.ServerEventRequest;

public final class JsonUtil {
  private JsonUtil() {
  }

  public static String toJson(JoinEventRequest request) {
    return "{"
        + field("serverId", request.serverId()) + ","
        + field("minecraftUuid", request.minecraftUuid().toString()) + ","
        + field("minecraftName", request.minecraftName()) + ","
        + numberField("occurredAt", request.occurredAt())
        + "}";
  }

  public static String toJson(LeaveEventRequest request) {
    return "{"
        + field("serverId", request.serverId()) + ","
        + field("minecraftUuid", request.minecraftUuid().toString()) + ","
        + numberField("occurredAt", request.occurredAt())
        + "}";
  }

  public static String toJson(ServerEventRequest request) {
    return "{"
        + field("serverId", request.serverId()) + ","
        + numberField("occurredAt", request.occurredAt())
        + "}";
  }

  public static String toJson(HeartbeatRequest request) {
    return "{"
        + field("serverId", request.serverId()) + ","
        + "\"players\":" + stringArray(request.players()) + ","
        + numberField("occurredAt", request.occurredAt())
        + "}";
  }

  private static String field(String name, String value) {
    return quote(name) + ":" + quote(value);
  }

  private static String numberField(String name, long value) {
    return quote(name) + ":" + value;
  }

  private static String quote(String value) {
    return "\"" + escape(value) + "\"";
  }

  private static String stringArray(Iterable<String> values) {
    StringBuilder builder = new StringBuilder("[");
    boolean first = true;
    for (String value : values) {
      if (!first) {
        builder.append(",");
      }
      builder.append(quote(value));
      first = false;
    }
    builder.append("]");
    return builder.toString();
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
