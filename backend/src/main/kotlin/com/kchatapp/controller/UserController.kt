package com.kchatapp.controller

import com.kchatapp.data.UserDataSource
import com.kchatapp.data.model.User

class UserController(
    private val userDataSource: UserDataSource
) {
    suspend fun createUser(user: User): User?{
        return userDataSource.createUser(user)
    }

    suspend fun getUserById(id: String): User?{
        return userDataSource.getUserById(id)
    }

    suspend fun getAllUsers(): List<User>{
        return userDataSource.getAllUsers()
    }
}