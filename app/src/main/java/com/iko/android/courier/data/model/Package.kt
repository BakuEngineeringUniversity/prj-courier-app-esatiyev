package com.iko.android.courier.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Package(
    @SerializedName("id")
    @Expose
    val id: Long? = null,
    @SerializedName("createdDate")
    @Expose
    val createdDate: String? = null,
    @SerializedName("packageName")
    @Expose
    val packageName: String? = null,
    @SerializedName("weight")
    @Expose
    val weight: Float? = null,
    @SerializedName("price")
    @Expose
    val price: Float? = null,
    @SerializedName("deliveryMethod")
    @Expose
    val deliveryMethod: String? = null,
    @SerializedName("deliverLatitude")
    @Expose
    var deliverLatitude: Double? = null,
    @SerializedName("deliverLongitude")
    @Expose
    var deliverLongitude: Double? = null,
    @SerializedName("deliverAddress")
    @Expose
    val deliverAddress: String? = null,
    @SerializedName("pickUpLatitude")
    @Expose
    var pickUpLatitude: Double? = null,
    @SerializedName("pickUpLongitude")
    @Expose
    var pickUpLongitude: Double? = null,
    @SerializedName("pickUpAddress")
    @Expose
    val pickUpAddress: String? = null,
    @SerializedName("receiverFullName")
    @Expose
    val receiverFullName: String? = null,
    @SerializedName("receiverPhone")
    @Expose
    val receiverPhone: String? = null,
    @SerializedName("receiverEmail")
    @Expose
    val receiverEmail: String? = null,
    @SerializedName("senderFullName")
    @Expose
    val senderFullName: String? = null,
    @SerializedName("senderPhone")
    @Expose
    val senderPhone: String? = null,
    @SerializedName("senderEmail")
    @Expose
    val senderEmail: String? = null,
    @SerializedName("courierFullName")
    @Expose
    val courierFullName: String? = null,
    @SerializedName("courierPhone")
    @Expose
    val courierPhone: String? = null,
    @SerializedName("courierEmail")
    @Expose
    val courierEmail: String? = null,
    @SerializedName("deliveryNote")
    @Expose
    val deliveryNote: String? = null,
    @SerializedName("deliveryStatus")
    @Expose
    var deliveryStatus: DeliveryStatus = DeliveryStatus.PACKAGE_CREATED,
    @SerializedName("deliveryHistory")
    @Expose
    val deliveryHistory: MutableList<DeliveryHistory>? = mutableListOf(),
//    // ignore
//    @SerializedName("customer")
//    val customer: Customer? = null,
//    // ignore
//    @SerializedName("courier")
//    val courier: Courier? = null
)