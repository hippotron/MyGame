# Stim24-LevServerKotlin

REST API на Kotlin + Ktor для управления играми. Состояние хранится в памяти.

## Запуск на Windows

### Вариант 1: Docker (проще всего)

1. Установи [Docker Desktop](https://www.docker.com/products/docker-desktop/) и запусти его.
2. Открой **PowerShell** или **CMD** в папке проекта (где лежит `Dockerfile`).
3. Выполни:

```powershell
docker build -t lev-server .
docker run -p 8080:8080 lev-server
```

Сервер будет доступен по адресу: http://localhost:8080

---

### Вариант 2: Локально (JDK + Gradle)

1. **Установи JDK 21**  
   - Скачай с [Adoptium (Eclipse Temurin)](https://adoptium.net/) или через winget:  
     `winget install EclipseAdoptium.Temurin.21.JDK`  
   - Убедись, что `java -version` в терминале показывает 21.

2. **Установи Gradle** (один из способов):
   - Через winget: `winget install Gradle.Gradle`
   - Или [скачай](https://gradle.org/releases/) и добавь в PATH.

3. В папке проекта выполни:

```powershell
gradle jar
java -jar build\libs\LevServerKotlin-1.0-SNAPSHOT.jar
```

4. Сервер запустится на http://localhost:8080

---

### Вариант 3: Через Gradle Wrapper (после того как Gradle уже установлен)

Один раз сгенерируй wrapper (нужен установленный Gradle):

```powershell
gradle wrapper
```

Дальше можно собирать и запускать без установленного Gradle:

```powershell
.\gradlew.bat jar
java -jar build\libs\LevServerKotlin-1.0-SNAPSHOT.jar
```

---

Сервер слушает порт **8080**.

### Swagger UI

Документация и тестирование API в браузере:  
**http://localhost:8080/swagger**

## API (v1/game)

| Метод | Путь | Описание |
|-------|------|----------|
| GET | v1/game/getGames | Список игр (gameId, date) |
| POST | v1/game/newgame | Создать игру (body: playerId, playerName) |
| POST | v1/game/addPlayer | Добавить игрока (body: playerId, playerName, gameId) |
| POST | v1/game/GetPlayers | Список игроков (body: gameId) |
| POST | v1/game/sendPole | Отправить расстановку (body: gameId, hod_player, economic, listPlayer, pole) |
| POST | v1/game/getPole | Получить расстановку (body: gameId) |
| POST | v1/game/gameover | Конец игры (body: gameId) |