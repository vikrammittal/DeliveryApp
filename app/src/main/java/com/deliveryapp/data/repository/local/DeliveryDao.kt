package com.deliveryapp.data.repository.local

import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.deliveryapp.domain.entity.Delivery
import io.reactivex.Single

@Dao
interface DeliveryDao {

    @Query("SELECT * FROM DELIVERY ORDER BY id ASC")
    fun queryDeliveries(): DataSource.Factory<Int, Delivery>

    @Query("SELECT * FROM DELIVERY WHERE id= :deliveryId")
    fun queryDelivery(deliveryId: Int): Single<Delivery>

    @Query("SELECT count(*) FROM DELIVERY")
    fun getCount(): Single<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDelivery(delivery: Delivery)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllDeliveries(deliveries: List<Delivery>)

    @Query("DELETE FROM DELIVERY")
    fun removeAll()
}