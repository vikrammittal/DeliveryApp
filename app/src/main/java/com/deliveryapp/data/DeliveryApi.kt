package com.deliveryapp.data

import javax.inject.Inject

class DeliveryApi @Inject constructor(private val deliveryEndpoint: DeliveryEndpoint) {

    fun getDeliveryList(offset: Int, limit: Int) = deliveryEndpoint.getDeliveryList(offset, limit)

}