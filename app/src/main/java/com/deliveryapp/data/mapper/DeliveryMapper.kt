package com.deliveryapp.data.mapper

import com.deliveryapp.data.response.DeliveryResponse
import com.deliveryapp.domain.entity.Delivery
import com.deliveryapp.domain.entity.DeliveryLocation
import javax.inject.Inject

class DeliveryMapper @Inject constructor() {

    fun map(responseList: List<DeliveryResponse>): List<Delivery> {
        return responseList.map { (map(it)) }
    }

    private fun map(response: DeliveryResponse): Delivery {
        return Delivery(
            id = response.id,
            imageUrl = response.imageUrl,
            description = response.description,
            location = DeliveryLocation(
                response.location.lat,
                response.location.lng,
                response.location.address
            )
        )
    }
}