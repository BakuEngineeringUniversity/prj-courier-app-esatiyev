package com.iko.android.courier.databases.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.iko.android.courier.databases.entities.User
import java.util.Optional

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): Optional<User>

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserByUserId(userId: Long): Optional<User>

    suspend fun getFirstUser(): Optional<User> {
        return getUserById(1)
    }

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()

    @Insert
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)
}
