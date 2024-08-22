package com.kchatapp.controller

import com.kchatapp.data.ChatDataSource
import com.kchatapp.data.UserDataSource
import com.kchatapp.data.model.Chat
import com.kchatapp.data.model.ChatItem

class ChatController(
    private val chatDataSource: ChatDataSource,
    private val userDataSource: UserDataSource
) {
    suspend fun getAllChats(userId: String): List<ChatItem> {
        return chatDataSource.getAllChats(userId).map { chat ->
            val receiverId = chat.userIds.first { it != userId }
            return@map ChatItem(userId, chat, userDataSource.getUserById(receiverId)!!.username)
        }
    }

    suspend fun createChat(chat: Chat): Chat {
        val user1Id = chat.userIds[0]
        val user2Id = chat.userIds[1]
        return chatDataSource.getChatByUserIds(user1Id, user2Id) ?: run {
            chatDataSource.createChat(user1Id, user2Id)
        }
    }

    suspend fun updateChat(chatId: String, lastMessage: String, lastMessageDate: Long): Chat {
        return chatDataSource.updateChat(chatId, lastMessage, lastMessageDate)
    }


}