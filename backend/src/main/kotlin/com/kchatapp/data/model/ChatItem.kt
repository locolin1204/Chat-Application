package com.kchatapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatItem(
    val senderId: String,
    val chat: Chat,
    val chatName: String
)
