package com.deliveryapp.presentation

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import com.deliveryapp.BaseApplication
import com.deliveryapp.di.application.ApplicationComponent
import com.deliveryapp.di.screen.ScreenModule

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    val screenComponent by lazy {
        getApplicationComponent().plus(ScreenModule(this))
    }

    private fun getApplicationComponent(): ApplicationComponent {
        return (application as BaseApplication).component
    }
}