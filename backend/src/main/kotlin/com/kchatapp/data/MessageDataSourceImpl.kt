package com.kchatapp.data

import com.kchatapp.data.model.Message
import org.litote.kmongo.and
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.lt

class MessageDataSourceImpl(
    db: CoroutineDatabase
) : MessageDataSource {

    private val messagesCollection = db.getCollection<Message>()

    override suspend fun getAllMessages(chatId: String, size: Int, lastMessageId: String): List<Message> {
        return messagesCollection.find(
            and(
                Message::chatId eq chatId,
                Message::_id lt lastMessageId
            )
        )
            .descendingSort(Message::timestamp)
            .limit(size)
            .toList()
            .sortedBy { it.timestamp }
    }

    override suspend fun getAllMessages(chatId: String, size: Int): List<Message> {
        return messagesCollection.find(
            and(
                Message::chatId eq chatId,
            )
        )
            .descendingSort(Message::timestamp)
            .limit(size)
            .toList()
            .sortedBy { it.timestamp }
    }

    override suspend fun insertMessage(message: Message) {
        messagesCollection.insertOne(message)
    }
}