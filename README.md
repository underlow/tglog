# tglog
Listens to all running docker containers and send selected log messages to telegram channel. Solves a problem of monitoring logs from self-hosted services if you don't want to use heavy monitoring solutions.

## Features
- containers can be filtered by name
- logs can be filtered by string (e.g. ERROR)
- sends container events (start, stop, die, etc.)

## Configuration
- `TELEGRAM_BOT_TOKEN` - bot token from BotFather (https://core.telegram.org/bots/tutorial)
- `TELEGRAM_CHAT_ID` - chat id where logs will be sent (add bot to channel and then https://stackoverflow.com/questions/32423837/telegram-bot-how-to-get-a-group-chat-id)

### Global filters

Exclude has higher priority than include. If you want to include all logs(or events), set include to `*` and exclude to empty list.

```yaml

tgbot:
  botToken: telegram_bot_token
  chatId: telegram_chat_id

logs:
  events:
    include: strings to monitor
    exclude: strings to exclude

container:
  events:
    include: events to monitor
    exclude: events to exclude
  names:
    include: container names to include
    exclude: container names to exclude

# per container filters, if not specified, global filters are used, works the same way as global filters but for specific container
containers:
  container:
    - name: "some container name"
      logs:
        events:
          include: "error"
          exclude:
      container:
        events:
          include: "*"
          exclude:
    - name: "another container name"
      logs:
        events:
          include: "error"
          exclude:
      container:
        events:
          include: "*"
          exclude:


```
example:

```yaml

tgbot:
  botToken: qwerty
  chatId: 1234567890
# will send only lines containing substring "error" 
logs:
  events:
    include: [error]
    exclude: debug
# will send events start and die for all containers except postgres and redis 
container:
  events:
    include: "start, die"
    exclude: "stop"
  names:
    include: ""*"
    exclude: "postgres, redis"

# will send all logs with "error" and no events for nginx container
containers:
  container:
    - name: nginx
      logs:
        events:
          include: "error"
          exclude:
      container:
        events:
          include:  
          exclude: 
  
  ``


## Running

### build and run in docker

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

### running with docker

```bash
docker run --rm  -v /var/run/docker.sock:/var/run/docker.sock  -e TELEGRAM_BOT_TOKEN=token -e TELEGRAM_CHAT_ID=chatId underlow/tglog
```

### running with docker-compose

```yaml 
version: '3.7'
services:
  tglog:
    build: .
    container_name: underlow/tglog
    restart: always
    environment:
      - TELEGRAM_BOT_TOKEN=your_bot_token
      - TELEGRAM_CHAT_ID=your_chat_id
      - <any additional env, see environment section>
      - CONTAINER_NAMES_EXCLUDE=some_container_name
      - CONTAINERS_CONTAINER_0_NAME=postgres
      - CONTAINERS_CONTAINER_0_LOGS_EVENTS_EXCLUDE="database,some_other_string"

    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
```


