package com.kchatapp.data

import com.kchatapp.data.model.Message

interface MessageDataSource {
    suspend fun getAllMessages(chatId: String, size: Int, lastMessageId: String): List<Message>

    suspend fun getAllMessages(chatId: String, size: Int): List<Message>

    suspend fun insertMessage(message: Message)
}