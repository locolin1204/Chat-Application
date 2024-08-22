package com.kchatapp.data

import com.kchatapp.data.model.Chat
import org.bson.BsonString
import org.bson.Document
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineDatabase

class ChatDataSourceImpl(
    db: CoroutineDatabase
) : ChatDataSource {
    private val chatCollection = db.getCollection<Chat>()

    override suspend fun getAllChats(senderId: String): List<Chat> {
        return try {
            chatCollection.find(Chat::userIds `in` senderId).toList()
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    override suspend fun getChatByChatId(chatId: String): Chat? {
        return try {
            chatCollection.findOneById(chatId)
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    override suspend fun getChatByUserIds(senderId: String, receiverId: String): Chat? {
        return try {
            chatCollection.findOne(Chat::userIds eq listOf(senderId, receiverId))
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    override suspend fun createChat(senderId: String, receiverId: String): Chat {
        return try {
            val result = chatCollection.insertOne(
                Chat(
                    userIds = listOf(senderId, receiverId)
                )
            )
            getChatByChatId((result.insertedId as BsonString).value)!!
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }

    override suspend fun updateChat(chatId: String, lastMessage: String, lastMessageDate: Long): Chat {
        return try {
            chatCollection.updateOneById(
                chatId,
                combine(
                    setValue(Chat::lastMessage, lastMessage),
                    setValue(Chat::lastMessageDate, lastMessageDate)
                )
            )
            getChatByChatId(chatId)!!
        } catch (e: Exception) {
            throw Exception(e.message)
        }
    }
}