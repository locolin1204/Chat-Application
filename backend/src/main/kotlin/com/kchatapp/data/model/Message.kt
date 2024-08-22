package com.kchatapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import java.util.*

@Serializable
data class Message(
    @SerialName("_id")
    val _id: String = ObjectId().toString(),
    val text: String,
    val userId: String,
    val chatId: String,
    val timestamp: Long
)
