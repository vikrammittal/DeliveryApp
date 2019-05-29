package com.deliveryapp.domain.usecase

import com.deliveryapp.domain.entity.Result
import com.deliveryapp.domain.repository.DeliveryRepository
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class DeliveryUseCase @Inject constructor(
    private val deliveryRepository: DeliveryRepository
) {
    fun fetchDeliveries(offset: Int, limit: Int): Observable<Result> {
        return deliveryRepository.getDeliveries(offset, limit)
            .toObservable()
            .map { Result.Success(it) as Result }
            .onErrorReturn { Result.Failure(it) }
            .startWith(Result.Loading)
    }

    fun getDeliveries() = deliveryRepository.getDeliveries()

    fun getDelivery(id: Int) = deliveryRepository.getDelivery(id)

    fun getCount(): Single<Int> = deliveryRepository.getCount()

    fun removeAll() = deliveryRepository.removeAll()
}