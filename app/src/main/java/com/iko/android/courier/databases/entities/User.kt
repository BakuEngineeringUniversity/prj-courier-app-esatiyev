package com.iko.android.courier.databases.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: Long? = null,
    var firstname: String?,
    var lastname: String?,
    var username: String?,
    var email: String?,
    var password: String?,
    var fin: String?,
    var serialNo: String?,
    var age: Int?,
    var gender: String?,
    var phone: String?,
    var address: String?,
    var ordersNumber: Int?,
    var expenses: Float?,
    var isCourier: Boolean?,
    var deliversNumber: Int?,
    var rating: Float?,
    var accessToken: String?,
    var refreshToken: String?,
    // ... other fields
)

