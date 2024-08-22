# Chat Application Backend

## How to run
### 1. Start MongoDB
```zsh
cd ..
mongod --dbpath db/data/db --logpath db/data/log/mongodb/ktor-chat-app.log
```
add the `--fork` flag for running in the background

### 2. Build and Run Ktor
```zsh
./gradlew run
```

## Endpoints
### Messages
| Methods | URL                     | Description                                |
|---------|-------------------------|--------------------------------------------|
| `WS`      | `/chat-socket/{chatId}` | Send and receive messages in the chat room |
| `GET`     | `/messages/{chatId}`    | Get all messages                           |

### Chat
| Methods | URL           | Description                          |
|---------|---------------|--------------------------------------|
| `GET`   | `/chats/{id}` | Get all chats available for the user |
| `POST`  | `/chat`       | Create new chat for the user         |

### User
| Methods | URL          | Description            |
|---------|--------------|------------------------|
| `GET`   | `/user/{id}` | Get user by ID         |
| `GET`   | `/users`     | Get all existing users |
| `POST`  | `/user`      | Create new user        |


## Project Structure

### Flow of information
`Routes` → `Controller` → `DataSource`

Similar to MVC <br>
`Controller` → `Service` → `Repository`