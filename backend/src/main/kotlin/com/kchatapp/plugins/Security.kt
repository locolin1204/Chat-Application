package com.kchatapp.plugins

import com.kchatapp.data.model.ChatSession
import io.ktor.server.application.*
import io.ktor.server.sessions.*
import io.ktor.util.*

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<ChatSession>("SESSION")
    }

    intercept(ApplicationCallPipeline.Features){
        if(call.sessions.get<ChatSession>() == null){
            val userId = call.parameters["userId"] ?: "Guest"
            call.sessions.set(ChatSession(userId, generateNonce()))
        }
    }
}
