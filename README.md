# tglog
Send log messages from all docker containers to telegram

## Running

### running in docker in local environment

First you have to build docker image: 
```bash
./gradlew clean build
docker build -t tglog:latest .
```

Then you can run it with:
```bash 
docker run --rm  -v /var/run/docker.sock:/var/run/docker.sock tglog
```
or in one line: 
```bash
./gradlew clean build && docker build -t tglog:latest . && docker run --rm  -v /var/run/docker.sock:/var/run/docker.sock tglog
```

### running with docker-compose

```yaml 
version: '3.7'
services:
  tglog:
    build: .
    container_name: tglog
    restart: always
    environment:
      - TELEGRAM_BOT_TOKEN=your_bot_token
      - TELEGRAM_CHAT_ID=your_chat_id
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
```


