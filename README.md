# AkkaHttpSseSample

Akka HTTPでPub/Sub + SSEなサンプルです。

以下のコマンドでサーバーを起動します。

```
$ sbt
sbt> run
```

終了するにはEnterキーをタイプします。

## Subscribe

```
GET http://localhost:8080/subscribe?channel=CHANNEL_NAME
```

`CHANNEL_NAME` は任意の文字列です。

## Publish

```
POST http://localhost:8080/publish

channel=CHANNEL_NAME&message=MESSAGE
```

`CHANNEL_NAME` で購読しているクライアントに `MESSAGE` を送ります。
