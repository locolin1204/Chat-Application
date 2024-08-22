package com.kchatapp.data

import com.kchatapp.data.model.User
import org.litote.kmongo.coroutine.CoroutineDatabase

class UserDataSourceImpl(
    db: CoroutineDatabase
): UserDataSource {

    private val usersCollection = db.getCollection<User>()

    override suspend fun getUserById(id: String): User? {
        return try{
            usersCollection.findOneById(id)
        } catch (e: Exception){
            throw Exception(e.message)
        }
    }

    override suspend fun getAllUsers(): List<User> {
        return try {
            usersCollection.find()
                .descendingSort(User::username)
                .toList()
        } catch (e: Exception){
            throw Exception(e.message)
        }
    }

    override suspend fun createUser(user: User): User? {
        return try {
            usersCollection.insertOne(user)
            getUserById(user._id)
        } catch (e: Exception){
            throw Exception(e.message)
        }
    }


}