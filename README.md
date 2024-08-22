# Chat Application
This project is a chat application built using `Angular` with `TypeScript` for frontend, `Ktor` with `Kotlin` for backend and `MongoDB` for database.

It supports real-time messaging via WebSocket and provides a RESTful API to retrieve the message history. 
The application manages user sessions to ensure organized communication.

# How To Run

## Frontend
```zsh
cd frontend && ng serve --open
```

## Backend 
```zsh
cd backend && ./gradlew run
```

## Database
```zsh
mongod --dbpath db/data/db --logpath db/data/log/mongodb/ktor-chat-app.log 
 ```
 add the `--fork` flag for running the database in the background