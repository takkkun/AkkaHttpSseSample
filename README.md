# AkkaHttpSseSample

Akka HTTPでPub/Sub + SSEなサンプルです。

## Redisサーバー起動

Pub/SubにはRedisを使用しています。あらかじめRedisのサーバーを起動しておいてください。

接続先のホスト名またはポート番号を変更したい場合は `src/main/scala/Bootstrap.scala` の `REDIS_HOST` または `REDIS_PORT` を変更してください。

## サーバー起動

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
