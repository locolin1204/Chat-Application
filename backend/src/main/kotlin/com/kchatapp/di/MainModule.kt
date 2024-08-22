package com.kchatapp.di

import com.kchatapp.controller.ChatController
import com.kchatapp.data.*
import com.kchatapp.controller.RoomController
import com.kchatapp.controller.UserController
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun mainModule(dbHost: String, dbPort: String, dbName: String) = module {
    single {
        val connectionString = "mongodb://$dbHost:$dbPort"
        print("connectionString: $connectionString")
        KMongo.createClient(connectionString)
            .coroutine
            .getDatabase(dbName)
    }
    single<MessageDataSource> { MessageDataSourceImpl(get()) }
    single<UserDataSource> { UserDataSourceImpl(get()) }
    single<ChatDataSource> { ChatDataSourceImpl(get()) }

    single { RoomController(get()) }
    single { UserController(get()) }
    single { ChatController(get(), get()) }
}