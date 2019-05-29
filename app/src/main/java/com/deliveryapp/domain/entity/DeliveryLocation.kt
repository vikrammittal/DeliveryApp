package com.deliveryapp.domain.entity

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
data class DeliveryLocation(
    val lat: Double,
    val lng: Double,
    val address: String
)