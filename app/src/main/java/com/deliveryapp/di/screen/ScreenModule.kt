package com.deliveryapp.di.screen

import com.deliveryapp.di.scope.PerScreen
import com.deliveryapp.presentation.BaseActivity
import com.deliveryapp.presentation.main.MainRouter
import dagger.Module
import dagger.Provides
import java.lang.ref.WeakReference

@Module
class ScreenModule(private val activity: BaseActivity) {

    @PerScreen
    @Provides
    fun providesActivity(): BaseActivity {
        return activity
    }

    @PerScreen
    @Provides
    fun providesMainRouter(): MainRouter {
        return MainRouter(WeakReference(activity))
    }
}