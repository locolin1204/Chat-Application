package com.kchatapp.data.model

import io.ktor.websocket.*

// Members inside the chatroom
data class Member(
    val userId: String,
    val sessionId: String,
    val socket: WebSocketSession
)