package com.kchatapp.data

import com.kchatapp.data.model.Chat

interface ChatDataSource {
    suspend fun getAllChats(senderId: String): List<Chat>

    suspend fun getChatByChatId(chatId: String): Chat?

    suspend fun getChatByUserIds(senderId: String, receiverId: String): Chat?

    suspend fun createChat(senderId: String, receiverId: String): Chat

    suspend fun updateChat(chatId: String, lastMessage: String, lastMessageDate: Long): Chat
}