package com.kchatapp.util

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*

object AppConfig {
    private val config = HoconApplicationConfig(ConfigFactory.load())

    val chatCollection: String = config.property("database.collection.chat").getString()
}