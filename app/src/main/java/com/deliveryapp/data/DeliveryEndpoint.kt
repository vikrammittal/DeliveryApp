package com.deliveryapp.data

import com.deliveryapp.data.response.DeliveryResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface DeliveryEndpoint {

    @GET("deliveries/")
    fun getDeliveryList(@Query("offset") offset: Int, @Query("limit") limit: Int): Single<List<DeliveryResponse>>

}