package com.kchatapp.data.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId


@Serializable
data class User(
    @SerialName("_id")
    val _id: String = ObjectId().toString(),
    val username: String
)
