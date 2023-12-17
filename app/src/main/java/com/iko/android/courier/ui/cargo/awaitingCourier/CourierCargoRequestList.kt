package com.iko.android.courier.ui.cargo.awaitingCourier

data class CourierCargoRequestList(
    val reviewerName: String,
    val deliveryType: String,
    val deliveryDate: String,
    val pickUpAddress: String,
    val deliverAddress: String,
    val weight: Float,
    val price: Float,
    val request: String
)