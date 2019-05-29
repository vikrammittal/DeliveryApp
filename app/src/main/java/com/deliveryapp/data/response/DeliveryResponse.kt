package com.deliveryapp.data.response

data class DeliveryResponse(
    val id: Int,
    val description: String,
    val imageUrl: String,
    val location: DeliveryLocationResponse
)