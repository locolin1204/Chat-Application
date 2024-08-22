package com.kchatapp.routes

import com.kchatapp.data.model.User
import com.kchatapp.controller.UserController
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.userRoutes() {
    val userController by inject<UserController>()
    routing {
        getUser(userController)
        getAllUsers(userController)
        createUser(userController)
    }
}

fun Route.getUser(userController: UserController) {
    get("/user/{id}") {
        try {
            call.parameters["id"]?.let { id ->
                userController.getUserById(id)?.let {
                    call.respond(it)
                } ?: run {
                    call.respondText("null")
                }
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
            call.respondText(e.message ?: "Error")
        }
    }
}

fun Route.getAllUsers(userController: UserController) {
    get("/users") {
        try {
            call.respond(userController.getAllUsers())
        } catch (e: Exception) {
            println("Error: ${e.message}")
            call.respondText(e.message ?: "Error")
        }
    }
}

fun Route.createUser(userController: UserController) {
    post("/user") {
        try {
            val user = call.receive<User>()
            userController.createUser(user)?.let {
                call.respond(it)
            } ?: run {
                call.respondText("null")
            }
        } catch (e: Exception) {
            println("Error: ${e.message}")
            call.respondText(e.message ?: "Error")
        }
    }
}