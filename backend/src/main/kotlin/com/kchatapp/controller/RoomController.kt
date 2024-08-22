package com.kchatapp.controller

import com.kchatapp.data.MessageDataSource
import com.kchatapp.data.model.Message
import com.kchatapp.data.model.Member
import com.kchatapp.exception.MemberAlreadyExistsException
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val messageDataSource: MessageDataSource
) {
    private val members = ConcurrentHashMap<String, Member>()
    private val logger = LoggerFactory.getLogger(RoomController::class.java)

    fun onJoin(
        userId: String,
        sessionId: String,
        socket: WebSocketSession
    ){
        if(members.containsKey(userId)){
            throw MemberAlreadyExistsException()
        }
        members[userId] = Member(
            userId = userId,
            sessionId = sessionId,
            socket = socket
        )
    }

    suspend fun sendMessage(chatId: String, senderId: String, message: String, timestamp: Long){
        val messageEntity = Message(
            text = message,
            userId = senderId,
            timestamp = timestamp,
            chatId = chatId
        )
        members.values.forEach { member ->
            val parsedMessage = Json.encodeToString(messageEntity)
            member.socket.send(Frame.Text(parsedMessage))
        }
        messageDataSource.insertMessage(messageEntity)
    }

    suspend fun getAllMessages(chatId: String, size: Int, lastMessageId: String?): List<Message> {
        lastMessageId?.let {
            logger.info("Getting message from chatId $chatId, size $size, lastMessageId $lastMessageId")
            return messageDataSource.getAllMessages(chatId, size, it)
        } ?: run {
            logger.info("Getting message from chatId $chatId, size $size without lastMessageId")
            return messageDataSource.getAllMessages(chatId, size)
        }
    }

    suspend fun tryDisconnect(username: String) {
        members[username]?.socket?.close()
        if(members.containsKey(username)){
            members.remove(username)
        }
    }

}