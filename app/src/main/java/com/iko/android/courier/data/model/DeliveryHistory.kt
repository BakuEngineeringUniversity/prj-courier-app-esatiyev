package com.iko.android.courier.data.model

data class DeliveryHistory(
    val status: DeliveryStatus? = null,
    val timestamp: String? = null,
    val packet: Package? = null
)