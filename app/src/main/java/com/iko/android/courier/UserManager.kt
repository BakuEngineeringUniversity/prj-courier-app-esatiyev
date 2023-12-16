package com.iko.android.courier


import com.iko.android.courier.data.model.Package
import com.iko.android.courier.data.model.Review

object UserManager {
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
    var ordersNumber: Int = 0
    var expenses: Float = 0f
    var deliversNumber: Int = 0
    var rating: Int = 0
    var deliveriesPackages: MutableList<Package>? = mutableListOf()
    var reviews: MutableList<Review>? = mutableListOf()
}