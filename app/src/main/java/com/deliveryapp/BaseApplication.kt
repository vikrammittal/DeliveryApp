package com.deliveryapp

import android.app.Application
import com.deliveryapp.di.application.*

open class BaseApplication : Application() {

    lateinit var component: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        inject()
    }

    open fun inject() {
        component = DaggerApplicationComponent.builder()
            .endpointModule(EndpointModule())
            .databaseModule(DatabaseModule(this))
            .build()
    }
}