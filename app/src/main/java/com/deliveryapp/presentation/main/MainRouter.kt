package com.deliveryapp.presentation.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.deliveryapp.presentation.deliverydetail.DeliveryDetailActivity
import java.lang.ref.WeakReference

/**
 * MainRouter handles navigation for the MainActivity
 */
class MainRouter(private val activityRef: WeakReference<Activity>) {

    companion object {
        const val DELIVERY_DETAIL = "delivery_detail"
    }
    
    fun navigate(route: String, bundle: Bundle = Bundle()) {
        if (route == DELIVERY_DETAIL) {
            showNextScreen(DeliveryDetailActivity::class.java, bundle)
        }
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun showNextScreen(clazz: Class<*>, bundle: Bundle?) {
        activityRef.get()?.startActivity(Intent(activityRef.get(), clazz).putExtras(bundle))
    }
}