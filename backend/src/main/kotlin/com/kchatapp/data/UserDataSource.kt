package com.kchatapp.data

import com.kchatapp.data.model.User

interface UserDataSource {
    suspend fun getUserById(id: String): User?

    suspend fun getAllUsers(): List<User>

    suspend fun createUser(user: User): User?
}