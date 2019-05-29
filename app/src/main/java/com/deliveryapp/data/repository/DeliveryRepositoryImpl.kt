package com.deliveryapp.data.repository

import com.deliveryapp.data.DeliveryApi
import com.deliveryapp.data.mapper.DeliveryMapper
import com.deliveryapp.domain.entity.Delivery
import com.deliveryapp.domain.repository.DeliveryRepository
import io.reactivex.Single

/**
 * DeliveryRepositoryImpl implements DeliveryRepository
 *
 * DeliveryRepository is responsible for retrieving the Delivery info from the api layer
 */
class DeliveryRepositoryImpl(
    private val deliveryApi: DeliveryApi,
    private val deliveryMapper: DeliveryMapper,
    private val deliveryDataSource: DeliveryDataSource
) : DeliveryRepository {

    override fun getDeliveries(offset: Int, limit: Int): Single<List<Delivery>> {
        return deliveryApi.getDeliveryList(offset, limit)
            .map {

                val list: List<Delivery> = deliveryMapper.map(it)
                if (offset == 0) {
                    deliveryDataSource.removeAll()
                }
                deliveryDataSource.addToLocal(list)
                return@map list
            }
    }

    override fun getDeliveries() = deliveryDataSource.getDeliveries()

    override fun addToLocal(deliveries: List<Delivery>) = deliveryDataSource.addToLocal(deliveries)

    override fun getDelivery(id: Int) = deliveryDataSource.getDelivery(id)

    override fun getCount() = deliveryDataSource.getCount()

    override fun removeAll() = deliveryDataSource.removeAll()
}