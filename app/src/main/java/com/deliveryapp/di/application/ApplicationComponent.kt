package com.deliveryapp.di.application

import com.deliveryapp.BaseApplication
import com.deliveryapp.di.screen.ScreenComponent
import com.deliveryapp.di.screen.ScreenModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [ApplicationModule::class, RepositoryModule::class,
        EndpointModule::class, DatabaseModule::class]
)
interface ApplicationComponent {

    fun inject(activity: BaseApplication)

    fun plus(screenModule: ScreenModule): ScreenComponent

}