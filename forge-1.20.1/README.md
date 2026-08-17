# Forge 1.20.1 discord-connector

Minecraft **1.20.1** + **Forge 47.4.10** の最小MOD（`/hello` コマンド）です。新規MODの出発点として使えます。

公式のセットアップ手順は同梱の [README.txt](README.txt) および [Forge ドキュメント（Getting Started）](https://docs.minecraftforge.net/en/1.20.1/gettingstarted/) を参照してください。

---

## 開発時に触る場所（早見表）

| 目的 | 主に編集するファイル |
|------|----------------------|
| MOD ID・表示名・バージョン・作者・説明 | `gradle.properties`（下記プロパティ名） |
| MOD IDをコードと一致させる | `src/main/java/com/example/discordconnector/DiscordConnectorForge.java` の `MODID` 定数 |
| ゲーム内の文言（翻訳） | `src/main/resources/assets/<mod_id>/lang/en_us.json` / `ja_jp.json` |
| MODメタ情報（依存関係など） | `src/main/resources/META-INF/mods.toml` |
| データパック説明（リソースパック相当の説明文） | `src/main/resources/pack.mcmeta`（ビルド時に `${mod_id}` が展開されます） |
| Minecraft / Forge バージョン | `gradle.properties` の `minecraft_version` / `forge_version` など |
| ビルド・実行タスク | `build.gradle` |
| VS Code のデバッグ起動 | `.vscode/launch.json`（MOD ID変更時は要更新。下記参照） |

---

## `gradle.properties` のテンプレート用プロパティ名

ビルド時に `mods.toml` や `pack.mcmeta` へ展開される値です。**ここを変えたら**、コード側の `MODID` とリソースパスも揃えてください。

| プロパティ名 | 意味 |
|--------------|------|
| `mod_id` | MOD ID（小文字英数字と `_`、先頭は英字） |
| `mod_name` | MOD一覧に出る表示名 |
| `mod_version` | MODのバージョン文字列 |
| `mod_license` | ライセンス表記（例: `MIT`） |
| `mod_group_id` | Maven 用グループID。Java のパッケージ（例: `com.example.mymod`）と揃えるのが一般的 |
| `mod_authors` | 作者表示用文字列 |
| `mod_description` | MOD説明（`\n` で改行可） |
| `minecraft_version` / `forge_version` | 使用する MC / Forge の版 |
| `minecraft_version_range` / `forge_version_range` / `loader_version_range` | 互換性のバージョン範囲 |
| `mapping_channel` / `mapping_version` | 開発用マッピング（既定は `official`） |

---

## MOD ID を変えたときに必ず揃えるもの

`mod_id` を例として `mymod` に変える場合のチェックリストです。

1. **`gradle.properties`** … `mod_id=mymod` および必要なら `mod_group_id` を変更。
2. **`DiscordConnectorForge.java`** … `public static final String MODID = "mymod";` と `@Mod` の引数が一致していること。
3. **パッケージ・クラス名**（任意）… グループIDに合わせて `com.example.mymod` のようにリネームするなら、ディレクトリ構造と `package` 宣言も合わせる。
4. **リソースパス** … `src/main/resources/assets/discord_connector/` を必要な MOD ID の名前空間にリネーム（MOD IDと名前空間を一致させるのが一般的）。
5. **`mods.toml`** … ファイル内の **`[[dependencies.discord_connector]]` を `[[dependencies.mymod]]` に2箇所とも変更**（テンプレートでは TOML の制約のため mod_id と連動したプレースホルダにしていません）。
6. **`.vscode/launch.json`** … `MOD_CLASSES` の先頭、`--mod` 引数、`forge.enabledGameTestNamespaces` などに **`discord_connector` がハードコードされている箇所**を新しい MOD ID に置き換え。
   または Gradle で実行構成を再生成し直す方法（`genVSCodeRuns` 等、環境に応じて）を取ると安全です。

---

## ソースコードの置き場所

| ファイル | 役割 |
|----------|------|
| `src/main/java/com/example/discordconnector/DiscordConnectorForge.java` | `@Mod` エントリ。イベント登録の起点。 |
| `src/main/java/com/example/discordconnector/DiscordCommand.java` | `/discord` コマンドの実装例。 |

新しい機能はこのパッケージ配下にクラスを追加し、`DiscordConnectorForge` のコンストラクタやイベント購読で登録します。

---

## リソース

| パス | 役割 |
|------|------|
| `src/main/resources/META-INF/mods.toml` | FML が読む MOD 定義。多くの項目は `gradle.properties` の `${...}` で埋まります。 |
| `src/main/resources/assets/<mod_id>/lang/*.json` | 言語キーと翻訳。 |
| `src/main/resources/pack.mcmeta` | リソースパック形式のメタ。`description` に `${mod_id}` が使われます。 |

データ生成を使う場合は `build.gradle` の `data` ランと `src/generated/resources/` が関わります（未使用なら無視して構いません）。

---

## よく使うコマンド

プロジェクトルート（この `README.md` があるフォルダ）で実行します。

```powershell
# IDE 用の実行構成生成（IntelliJ の例）
.\gradlew.bat genIntellijRuns

# クライアント起動（Gradle から）
.\gradlew.bat runClient

# コンパイル確認
.\gradlew.bat compileJava

# 配布用 JAR（build\libs\）
.\gradlew.bat build
```

**推奨:** MOD 本体の開発は **JDK 17** を使ってください（1.18+ Forge の想定）。`java -version` で確認できます。

---

## GitHub に上げるときの目安

コミットに含めてよいもの: `src/`、`build.gradle`、`settings.gradle`、`gradle.properties`、`gradlew*`、`gradle/wrapper/`、`.gitignore`、任意で `.vscode/`。

含めないことが多いもの: `build/`、`run/`、`run-data/`、`bin/`、`.gradle/`（ルートの `.gitignore` で除外されているか確認）。

---

## ライセンス

`gradle.properties` の `mod_license` とリポジトリの `LICENSE` を用途に合わせて変更してください。
