package com.deliveryapp.data.repository.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.deliveryapp.domain.Constants
import com.deliveryapp.domain.entity.Delivery

@Database(entities = [(Delivery::class)], version = Constants.DB_VERSION, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun deliveryDao(): DeliveryDao
}