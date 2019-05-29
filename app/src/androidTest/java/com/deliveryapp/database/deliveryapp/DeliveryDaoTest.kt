package com.deliveryapp.database.deliveryapp

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.deliveryapp.data.repository.local.Database
import com.deliveryapp.data.repository.local.DeliveryDao
import com.deliveryapp.domain.entity.Delivery
import com.deliveryapp.domain.entity.DeliveryLocation
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeliveryDaoTest {

    private var deliveryDao: DeliveryDao? = null

    private lateinit var db: Database


    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), Database::class.java).build()
        deliveryDao = db.deliveryDao()
    }

    @Test
    fun insertSingleTest() {
        val delivery = Delivery(
            1,
            "",
            "",
            DeliveryLocation(1.0, 1.1, "")
        )
        deliveryDao?.removeAll()
        deliveryDao?.getCount()?.test()?.assertValue(0)
        deliveryDao?.insertDelivery(delivery)
        deliveryDao?.getCount()?.test()?.assertValue(1)
    }

    @Test
    fun insertListTest() {
        val list = ArrayList<Delivery>()
        for (i in 0 until 10) {
            list.add(
                Delivery(
                    i + 1,
                    "",
                    "",
                    DeliveryLocation(1.0, 1.1, "")
                )
            )
        }

        deliveryDao?.removeAll()
        deliveryDao?.getCount()?.test()?.assertValue(0)
        deliveryDao?.insertAllDeliveries(list)
        deliveryDao?.getCount()?.test()?.assertValue(list.size)
    }

    @Test
    fun getDeliveryTest() {
        val delivery = Delivery(
            1,
            "",
            "",
            DeliveryLocation(1.0, 1.1, "")
        )
        deliveryDao?.insertDelivery(delivery)
        deliveryDao?.queryDelivery(delivery.id)?.test()?.assertValueAt(0, delivery)
    }

    @After
    fun tearDown() {
        db.close()
    }

}