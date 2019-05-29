package com.deliveryapp.di

import com.deliveryapp.BaseApplication
import com.deliveryapp.di.application.DatabaseModule

class TestApplication : BaseApplication() {

    override fun inject() {
        component = DaggerTestApplicationComponent.builder()
            .testEndpointModule(TestEndpointModule())
            .databaseModule(DatabaseModule(this))
            .build()
    }

}