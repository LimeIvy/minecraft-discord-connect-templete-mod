# discord-connect-template

`discord-connect-template` is a Minecraft Forge 1.20.1 mod template for connecting Minecraft server events to a customizable Discord integration API.

The mod sends server and player events to an API selected by the server administrator. It does not include a hosted API, database, database credentials, or private server configuration.

## Environment

- Minecraft: `1.20.1`
- Forge: `47.4.10`
- Java: `17`
- Mod ID: `discord_connector`

The mod must currently be installed on both the dedicated server and the connecting clients with the same version.

## Features

- Server start and stop notifications
- Player join and leave notifications
- Heartbeat requests every five minutes
- Discord account linking with `/discord link`
- Bearer-authenticated HTTPS API requests
- Asynchronous API requests on a dedicated executor
- Limited retries for `429` and `5xx` responses
- English and Japanese in-game messages

## Configuration

This mod uses a Forge server configuration. After starting the server once, edit the generated file for the world:

```text
run/saves/<world>/serverconfig/discord_connector-server.toml
```

The public template leaves `api_url` and `api_key` empty. API requests are skipped until an API URL is configured.

```toml
[discord_connector]
server_id = "template-server"
api_url = ""
api_key = ""
```

When using your own compatible API, configure it as follows:

```toml
[discord_connector]
server_id = "my-server"
api_url = "https://your-api.example.com"
api_key = "replace_with_your_token"
```

Keep `api_key` private. Do not commit it to Git or include it in a distributed JAR. Database connection strings, database passwords, and backend secrets must remain on the API server.

## In-Game Usage

Run this command in Minecraft:

```text
/discord link
```

The mod requests a temporary linking code from the API and displays the Discord command to use:

```text
/link code:YOUR_CODE
```

The linking code is valid for 10 minutes.

## API Contract

All requests use the following headers:

```http
Content-Type: application/json
Authorization: Bearer <api_key>
```

### Server Start

```http
POST /v1/minecraft/events/server-start
```

```json
{
  "serverId": "my-server",
  "occurredAt": 1786959377
}
```

### Server Stop

```http
POST /v1/minecraft/events/server-stop
```

```json
{
  "serverId": "my-server",
  "occurredAt": 1786959472
}
```

### Player Join

```http
POST /v1/minecraft/events/join
```

```json
{
  "serverId": "my-server",
  "minecraftUuid": "a6c3a42e-66ff-4137-a862-dcab26221947",
  "minecraftName": "PlayerName",
  "occurredAt": 1786959466
}
```

### Player Leave

```http
POST /v1/minecraft/events/leave
```

```json
{
  "serverId": "my-server",
  "minecraftUuid": "a6c3a42e-66ff-4137-a862-dcab26221947",
  "minecraftName": "PlayerName",
  "occurredAt": 1786959472
}
```

### Heartbeat

The mod sends a heartbeat every five minutes while the server is running.

```http
POST /v1/minecraft/heartbeat
```

```json
{
  "serverId": "my-server",
  "players": [
    "a6c3a42e-66ff-4137-a862-dcab26221947"
  ],
  "occurredAt": 1786959677
}
```

### Link Code

```http
POST /v1/minecraft/link-code
```

```json
{
  "serverId": "my-server",
  "minecraftUuid": "a6c3a42e-66ff-4137-a862-dcab26221947",
  "minecraftName": "PlayerName"
}
```

Expected response:

```json
{
  "code": "123456",
  "expiresAt": 1786906200
}
```

## Build

```powershell
.\gradlew.bat clean build
```

The JAR is generated at:

```text
build/libs/discord-connect-template-forge-1.20.1-0.1.0.jar
```

## Do Not Include

- A real API URL
- A real `api_key`
- Database connection strings
- Database usernames or passwords
- `.env` files
- `run/`
- `build/`
- `.gradle/`
