# discord-connector Forge 1.20.1

Minecraft サーバーのイベントを Discord Connector API へ送信する Forge サーバーサイド Mod です。

対応環境:

- Minecraft: `1.20.1`
- Forge: `47.4.10`
- Java: `17`
- Mod ID: `discord_connector`
- 生成 jar: `build/libs/discord-connector-forge-1.20.1-0.1.0.jar`

## 機能

- サーバー起動通知
- サーバー停止通知
- プレイヤー参加通知
- プレイヤー退出通知
- 5分ごとの Heartbeat
- Discord 連携コード発行コマンド `/discord link`
- Bearer 認証付き HTTPS API 通信
- 専用スレッド `discord-connector-api` による非同期 API 通信
- 通信エラー、`429`、`5xx` に対する回数制限付き retry

## プロジェクト構成

```text
forge-1.20.1/
  common/
    src/main/java/com/example/discordconnector/
      api/
      logging/
      model/
      service/
      util/
  src/main/java/com/example/discordconnector/
    config/
    event/
    logging/
    service/
    DiscordCommand.java
    DiscordConnectorForge.java
```

`common` には、将来 NeoForge など別 loader でも使える Java のみの処理を置いています。Forge や Minecraft のクラスには依存させません。

Forge 固有の config 登録、イベントハンドラ、コマンド登録、Minecraft クラスから common DTO への変換は `src/main/java` 側に置きます。

## 設定

この Mod は Forge の SERVER config を使います。通常のサーバー実行時は、ワールド配下の server config に生成されるファイルを編集します。

例:

```text
run/saves/<world>/serverconfig/discord_connector-server.toml
```

設定例:

```toml
[discord_connector]
server_id = "oceanblock2"
api_url = "https://minecraft-discord-connector-api.limeivy1221.workers.dev"
api_key = "mc_xxxxxxxxxxxxxxxxx"
```

注意:

- `api_url` には必ず `https://` を含めてください。
- `api_key` は秘密情報です。Git にコミットしないでください。
- `run/` は `.gitignore` で除外されています。

## API 通信

すべての API 通信には以下のヘッダーを付けます。

```http
Content-Type: application/json
Authorization: Bearer <api_key>
```

### サーバー起動

```http
POST /v1/minecraft/events/server-start
```

```json
{
  "serverId": "oceanblock2",
  "occurredAt": 1786959377
}
```

### サーバー停止

```http
POST /v1/minecraft/events/server-stop
```

```json
{
  "serverId": "oceanblock2",
  "occurredAt": 1786959472
}
```

### プレイヤー参加

```http
POST /v1/minecraft/events/join
```

```json
{
  "serverId": "oceanblock2",
  "minecraftUuid": "a6c3a42e-66ff-4137-a862-dcab26221947",
  "minecraftName": "Lime_Ivy",
  "occurredAt": 1786959466
}
```

### プレイヤー退出

```http
POST /v1/minecraft/events/leave
```

```json
{
  "serverId": "oceanblock2",
  "minecraftUuid": "a6c3a42e-66ff-4137-a862-dcab26221947",
  "occurredAt": 1786959472
}
```

### Heartbeat

サーバー起動中、5分ごとに送信します。

```http
POST /v1/minecraft/heartbeat
```

```json
{
  "serverId": "oceanblock2",
  "players": [
    "a6c3a42e-66ff-4137-a862-dcab26221947"
  ],
  "occurredAt": 1786959677
}
```

### Discord 連携コード

Minecraft 内で以下を実行します。

```text
/discord link
```

Mod は以下を送信します。

```http
POST /v1/minecraft/link-code
```

```json
{
  "serverId": "oceanblock2",
  "minecraftUuid": "a6c3a42e-66ff-4137-a862-dcab26221947",
  "minecraftName": "Lime_Ivy"
}
```

期待する API レスポンス:

```json
{
  "success": true,
  "data": {
    "code": "123456",
    "expiresAt": 1786906200
  }
}
```

プレイヤーには、Discord 側で以下を実行するよう案内します。

```text
/link code:123456
```

## ビルド

この README があるディレクトリで実行します。

```powershell
.\gradlew.bat clean build
```

成功時:

```text
BUILD SUCCESSFUL
```

jar は以下に生成されます。

```text
build/libs/discord-connector-forge-1.20.1-0.1.0.jar
```

Forge 用 jar には、`common` の class も同梱されます。

## 開発用サーバー起動

```powershell
.\gradlew.bat runServer
```

EULA で停止した場合は、以下を編集します。

```text
run/eula.txt
```

次のように設定してください。

```text
eula=true
```

## 動作確認チェックリスト

Forge 1.20.1 サーバーに jar を配置したあと、以下を確認します。

- サーバー起動 API が `2xx` を返す
- プレイヤー参加 API が `2xx` を返す
- プレイヤー退出 API が `2xx` を返す
- Heartbeat API が `2xx` を返す
- サーバー停止 API が `2xx` を返す
- `/discord link` で6桁コードが表示される
- API 完了ログのスレッド名が `discord-connector-api` になっている

成功ログ例:

```text
[discord-connector-api/INFO] [co.ex.di.DiscordConnectorForge/]: API request completed: path=/v1/minecraft/events/join, server_id=oceanblock2, status=200, attempts=1
```

## Git 管理しないもの

以下はコミットしないでください。

- `run/`
- `build/`
- `.gradle/`
- 実際の `api_key`

これらは `.gitignore` で除外されています。
