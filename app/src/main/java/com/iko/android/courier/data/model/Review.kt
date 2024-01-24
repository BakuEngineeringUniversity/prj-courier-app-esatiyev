package com.iko.android.courier.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("id")
    @Expose
    val id: Long? = null,
    @SerializedName("date")
    @Expose
    val date: String? = null,
    @SerializedName("comment")
    @Expose
    val comment: String? = null,
    @SerializedName("rating")
    @Expose
    val rating: Float? = null,
    @SerializedName("reviewerFullName")
    @Expose
    val reviewerFullName: String? = null,
    // ignore
//    @SerializedName("courier")
//    @Expose
//    val courier: Courier? = null
)