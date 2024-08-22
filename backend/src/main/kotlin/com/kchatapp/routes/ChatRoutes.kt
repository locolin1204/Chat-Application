package com.kchatapp.routes

import com.kchatapp.controller.ChatController
import com.kchatapp.data.model.Chat
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.chatRoutes() {
    val chatController by inject<ChatController>()
    routing {
        getAllChats(chatController)
        createChat(chatController)
    }
}

fun Route.getAllChats(chatController: ChatController) {
    get("/chats/{id}") {
        try {
            val senderId = call.parameters["id"]!!
            call.respond(chatController.getAllChats(senderId))
        } catch (e: Exception){
            println("Error: ${e.message}")
            call.respondText(e.message ?: "Error")
        }
    }
}

fun Route.createChat(chatController: ChatController){
    post("/chat"){
        try {
            val chat = call.receive<Chat>()
            call.respond(chatController.createChat(chat))
        } catch (e: Exception){
            println("Error: ${e.message}")
            call.respondText(e.message ?: "Error")
        }
    }
}