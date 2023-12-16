package com.iko.android.courier.ui.cargo.list

import java.io.Serializable

data class OwnCargoList(
    val packageId: String,
    val reviewerName: String,
    val deliveryType: String,
    val deliveryDate: String,
    val pickUpAddress: String,
    val deliverAddress: String,
    val weight: Float,
    val price: Float,
    val request: String

) : Serializable