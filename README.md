# Discord Connect Template

Discord Connect Template is a Minecraft Forge mod template that forwards Minecraft server events to an API chosen and hosted by the server administrator.

It is intended for users who want to connect Minecraft servers with their own database, Discord bot, dashboard, or other services. The mod does not include a hosted API, database, Discord bot, database credentials, or private server configuration.

## Available Implementations

| Implementation | Status | Documentation |
| --- | --- | --- |
| Minecraft Forge 1.20.1 | Available | [Forge 1.20.1 README](forge-1.20.1/README.md) |
| Minecraft NeoForge 1.21.1 | Planned | Not available yet |

The repository is organized by Minecraft version and mod loader. Future implementations can be added as separate directories such as `neoforge-1.21.1/`.

## How It Works

```text
Minecraft server
        |
        |  HTTPS JSON requests
        v
Your Discord Connector API
        |
        +--> Database
        +--> Discord bot
        +--> Web dashboard or other services
```

The Minecraft mod is responsible for collecting Minecraft events and sending them to the API. Your API is responsible for authentication, validation, persistence, Discord integration, and any application-specific logic.

This separation allows the same mod to be used with different databases, Discord bots, hosting providers, and server configurations.

## Example: Tracking Playtime Across Multiple Servers

You can connect multiple Minecraft servers to the same API by assigning a different `server_id` to each server:

```toml
# Server A
server_id = "survival"

# Server B
server_id = "creative"
```

Both servers send player join and leave events to the same API. The API can then:

1. Create a play session when a player joins a server.
2. Close the session when the player leaves.
3. Calculate the session duration from the recorded timestamps.
4. Store the session with the Minecraft UUID and `server_id`.
5. Add sessions from all servers to calculate a player's total playtime.
6. Let a Discord bot show commands such as total playtime, server-specific playtime, or current online status.

For example, the database could store records like this:

```text
player_uuid | server_id | joined_at           | left_at             | duration_seconds
------------|-----------|---------------------|---------------------|-----------------
player-uuid | survival  | 2026-08-20 10:00:00 | 2026-08-20 11:30:00 | 5400
player-uuid | creative  | 2026-08-20 12:00:00 | 2026-08-20 12:45:00 | 2700
```

The API can calculate a total of 8,100 seconds for this player without requiring the Minecraft mod to know anything about the database or Discord bot.

When implementing this use case, handle server crashes and disconnects by using heartbeat events or a timeout to close stale sessions.

## Supported Events

The Forge 1.20.1 implementation currently sends:

- Server start events
- Server stop events
- Player join events
- Player leave events
- Heartbeat events every five minutes
- Discord account linking code requests

## In-Game Usage

Run the following command in Minecraft:

```text
/discord link
```

The mod requests a temporary code from the API and displays a command for the user to run in Discord:

```text
/link code:YOUR_CODE
```

The API controls code generation and validation. The code is valid for 10 minutes in the Forge 1.20.1 implementation.

## Configuration

The current Forge implementation uses a server configuration file generated after the first server start:

```text
run/saves/<world>/serverconfig/discord_connector-server.toml
```

Example configuration:

```toml
[discord_connector]
server_id = "my-server"
api_url = "https://your-api.example.com"
api_key = "replace_with_your_token"
```

The public template uses empty values by default. API communication is skipped until `api_url` is configured.

The current implementation must be installed on both the dedicated server and connecting clients with the same version.

## API Contract

Requests use JSON and Bearer authentication:

```http
Content-Type: application/json
Authorization: Bearer <api_key>
```

The Forge 1.20.1 implementation uses these endpoints:

```text
POST /v1/minecraft/events/server-start
POST /v1/minecraft/events/server-stop
POST /v1/minecraft/events/join
POST /v1/minecraft/events/leave
POST /v1/minecraft/heartbeat
POST /v1/minecraft/link-code
```

See [the Forge implementation README](forge-1.20.1/README.md) for request and response examples.

## Security and Privacy

This repository intentionally does not contain:

- A production API URL
- API keys or bearer tokens
- Database connection strings
- Database usernames or passwords
- Discord bot tokens
- `.env` files
- Local server data or generated build output

Keep API keys and Discord bot tokens on the server that hosts your API. Do not place them in the mod JAR or commit them to Git.

## Repository Structure

```text
.
|-- forge-1.20.1/       # Forge implementation for Minecraft 1.20.1
|-- neoforge-1.21.1/    # Planned NeoForge implementation
|-- docs/               # Project-wide documentation
`-- README.md           # User-facing project overview
```

## License

See [LICENSE.txt](forge-1.20.1/LICENSE.txt) for license information.
