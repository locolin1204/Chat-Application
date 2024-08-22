package com.kchatapp.routes

import com.kchatapp.controller.ChatController
import com.kchatapp.exception.MemberAlreadyExistsException
import com.kchatapp.controller.RoomController
import com.kchatapp.data.model.ChatSession
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import org.koin.ktor.ext.inject
import org.slf4j.LoggerFactory
import java.lang.Exception

val logger = LoggerFactory.getLogger("MessageRoutes")

fun Application.messageRoutes() {
    val roomController by inject<RoomController>()
    val chatController by inject<ChatController>()
    routing {
        chatSocket(roomController, chatController)
        getAllMessages(roomController)
    }
}

fun Route.chatSocket(roomController: RoomController, chatController: ChatController) {
    webSocket("/chat-socket/{chatId}") {
        val chatId = call.parameters["chatId"]!!
        val session = call.sessions.get<ChatSession>()
        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session."))
            return@webSocket
        }

        try {
            roomController.onJoin(
                userId = session.userId,
                sessionId = session.sessionId,
                socket = this
            )
            println("session: $session")
            incoming.consumeEach { frame ->
                println("frame: $frame")
                if (frame is Frame.Text) {
                    println("frame.readText(): ${frame.readText()}")
                    val timestamp = System.currentTimeMillis()
                    roomController.sendMessage(
                        chatId = chatId,
                        senderId = session.userId,
                        message = frame.readText(),
                        timestamp = timestamp
                    )
                    chatController.updateChat(chatId, frame.readText(), timestamp)
                }
            }
        } catch (e: MemberAlreadyExistsException) {
            call.respond(HttpStatusCode.Conflict)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            roomController.tryDisconnect(session.userId)
        }
    }

}

fun Route.getAllMessages(roomController: RoomController) {
    get("/messages/{chatId}") {
        val lastMessageId = call.request.queryParameters["lastMessageId"]
        call.parameters["chatId"]?.let { chatId ->
            val size = call.request.queryParameters["size"]?.toInt() ?: 10
            call.respond(
                HttpStatusCode.OK,
                roomController.getAllMessages(chatId, size, lastMessageId)
            )
        }

    }
}

