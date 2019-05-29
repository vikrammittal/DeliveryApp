package com.deliveryapp.data.repository

import com.deliveryapp.data.repository.local.DeliveryDao
import com.deliveryapp.domain.entity.Delivery
import io.reactivex.Single
import javax.inject.Inject

class DeliveryDataSource @Inject constructor(private var deliveryDao: DeliveryDao) {

    fun getDeliveries() = deliveryDao.queryDeliveries()

    fun addToLocal(deliveries: List<Delivery>) = deliveryDao.insertAllDeliveries(deliveries)

    fun getDelivery(id: Int) = deliveryDao.queryDelivery(id)

    fun getCount(): Single<Int> = deliveryDao.getCount()

    fun removeAll() = deliveryDao.removeAll()
}