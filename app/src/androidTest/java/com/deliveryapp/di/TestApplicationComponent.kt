package com.deliveryapp.di

import com.deliveryapp.BaseApplication
import com.deliveryapp.di.application.*
import com.deliveryapp.di.screen.ScreenComponent
import com.deliveryapp.di.screen.ScreenModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [ApplicationModule::class, RepositoryModule::class,
        TestEndpointModule::class, DatabaseModule::class]
)
interface TestApplicationComponent : ApplicationComponent {

    override fun inject(activity: BaseApplication)

    override fun plus(screenModule: ScreenModule): ScreenComponent

}