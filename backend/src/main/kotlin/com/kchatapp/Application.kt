package com.kchatapp

import com.kchatapp.di.mainModule
import com.kchatapp.plugins.*
import com.kchatapp.routes.chatRoutes
import com.kchatapp.routes.messageRoutes
import com.kchatapp.routes.userRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import org.koin.ktor.plugin.Koin


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    val dbHost = environment.config.propertyOrNull("database.mongodb.host")?.getString() ?: "localhost"
    val dbPort = environment.config.propertyOrNull("database.mongodb.port")?.getString() ?: "27107"
    val dbName = environment.config.propertyOrNull("database.mongodb.dbname")?.getString() ?: "chatapp_db"

    install(CORS) {
        allowHost("localhost:4200")
        allowHeader(HttpHeaders.ContentType)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
    }
    install(Koin) {
        modules(mainModule(dbHost = dbHost, dbPort = dbPort, dbName = dbName))
    }
    configureSockets()
    configureSecurity()
    configureMonitoring()
    configureSerialization()
//    configureRouting()
    chatRoutes()
    messageRoutes()
    userRoutes()
}
