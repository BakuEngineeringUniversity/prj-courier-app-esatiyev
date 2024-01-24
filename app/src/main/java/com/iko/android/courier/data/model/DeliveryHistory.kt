package com.iko.android.courier.data.model

data class DeliveryHistory(
    val id: Long? = null,
    val status: DeliveryStatus? = null,
    val timestamp: String? = null,
    // ignore
//    val packet: Package? = null
)