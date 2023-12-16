package com.iko.android.courier.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Courier(
    @SerializedName("id")
    @Expose
    val id: Long? = null,
    @SerializedName("firstname")
    @Expose
    val firstname: String? = null,
    @SerializedName("lastname")
    @Expose
    val lastname: String? = null,
    @SerializedName("username")
    @Expose
    val username: String? = null,
    @SerializedName("email")
    @Expose
    val email: String? = null,
    @SerializedName("password")
    @Expose
    val password: String? = null,
    @SerializedName("fin")
    @Expose
    val fin: String? = null,
    @SerializedName("serialNo")
    @Expose
    val serialNo: String? = null,
    @SerializedName("age")
    @Expose
    val age: Int? = null,
    @SerializedName("gender")
    @Expose
    val gender: String? = null,
    @SerializedName("phone")
    @Expose
    val phone: String? = null,
    @SerializedName("address")
    @Expose
    val address: String? = null,
    @SerializedName("deliveriesPackages")
    @Expose
    val deliveriesPackages: MutableList<Package>? = mutableListOf(),
    @SerializedName("reviews")
    @Expose
    val reviews: MutableList<Review>? = mutableListOf()
)