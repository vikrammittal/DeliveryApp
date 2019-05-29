package com.deliveryapp.di.screen

import com.deliveryapp.di.scope.PerScreen
import com.deliveryapp.presentation.deliverydetail.DeliveryDetailActivity
import com.deliveryapp.presentation.main.MainActivity
import dagger.Subcomponent

@PerScreen
@Subcomponent(modules = [ScreenModule::class])
interface ScreenComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(deliveryDetailActivity: DeliveryDetailActivity)
}