package com.deliveryapp.di.application

import com.deliveryapp.data.DeliveryApi
import com.deliveryapp.data.mapper.DeliveryMapper
import com.deliveryapp.data.repository.DeliveryDataSource
import com.deliveryapp.data.repository.DeliveryRepositoryImpl
import com.deliveryapp.domain.repository.DeliveryRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideDeliveryRepository(
        deliveryApi: DeliveryApi,
        deliveryMapper: DeliveryMapper,
        deliveryDataSource: DeliveryDataSource
    ): DeliveryRepository {
        return DeliveryRepositoryImpl(deliveryApi, deliveryMapper, deliveryDataSource)
    }
}