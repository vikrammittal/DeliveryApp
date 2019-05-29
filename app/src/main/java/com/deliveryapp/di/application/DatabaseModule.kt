package com.deliveryapp.di.application

import android.arch.persistence.room.Room
import com.deliveryapp.BaseApplication
import com.deliveryapp.data.repository.local.Database
import com.deliveryapp.data.repository.local.DeliveryDao
import com.deliveryapp.utils.Constants
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class DatabaseModule(private val application: BaseApplication) {
    @Singleton
    @Provides
    fun provideDatabase(): Database {
        return Room.databaseBuilder(application, Database::class.java, Constants.DATABASE_NAME).build()
    }

    @Provides
    fun provideDeliveryDao(database: Database): DeliveryDao {
        return database.deliveryDao()
    }
}