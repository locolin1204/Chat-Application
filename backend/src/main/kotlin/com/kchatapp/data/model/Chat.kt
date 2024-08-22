package com.kchatapp.data.model

import com.mongodb.DBRef
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId

@Serializable
data class Chat(
    @SerialName("_id")
    val _id: String = ObjectId().toString(),
    val lastMessage: String? = null,
    @Contextual
    val lastMessageDate: Long? = null,
    val userIds: List<String>,
//    val userIds: List<DBRef>
//    val users: List<User>,
)
