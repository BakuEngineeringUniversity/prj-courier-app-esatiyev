package com.iko.android.courier

import com.iko.android.courier.data.model.Courier
import com.iko.android.courier.data.model.Customer
import com.iko.android.courier.data.model.DeliveryHistory
import com.iko.android.courier.data.model.DeliveryStatus


object PackageManager {
    var id: Long? = null
    var createdDate: String? = null
    var packageName: String? = null
    var weight: Float? = null
    var price: Float? = null
    var deliveryMethod: String? = null
    var deliverAddress: String? = null
    var pickUpAddress: String? = null
    var receiverFullName: String? = null
    var receiverPhone: String? = null
    var receiverEmail: String? = null
    var senderFullName: String? = null
    var senderPhone: String? = null
    var senderEmail: String? = null
    var deliveryNote: String? = null
    var deliveryStatus: DeliveryStatus = DeliveryStatus.PACKAGE_CREATED
    var deliveryHistory: MutableList<DeliveryHistory>? = mutableListOf()
    var customer: Customer? = null
    var courier: Courier? = null
}