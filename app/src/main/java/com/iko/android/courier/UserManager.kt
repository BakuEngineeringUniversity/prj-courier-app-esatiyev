package com.iko.android.courier


import com.iko.android.courier.data.model.Package
import com.iko.android.courier.data.model.Review

object UserManager {
    fun getFullName(): String {
        return "$firstname $lastname"
    }

    var accessToken: String? = null
    var refreshToken: String? = null
    var id: Long? = null
    var firstname: String? = null
    var lastname: String? = null
    var username: String? = null
    var email: String? = null
    var password: String? = null
    var fin: String? = null
    var serialNo: String? = null
    var age: Int? = null
    var gender: String? = null
    var phone: String? = null
    var address: String? = null
    var packages: MutableList<Package>? = mutableListOf()
    var ordersNumber: Int? = null
    var expenses: Float? = null
    var isCourier: Boolean? = false
    var deliversNumber: Int? = null
    var rating: Int? = null
    var deliveriesPackages: MutableList<Package>? = mutableListOf()
    var reviews: MutableList<Review>? = mutableListOf()
}