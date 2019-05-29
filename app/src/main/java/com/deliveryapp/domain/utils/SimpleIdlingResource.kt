package com.deliveryapp.domain.utils

import android.support.test.espresso.IdlingResource

class SimpleIdlingResource(private var name: String) : IdlingResource {

    private var resourceCallback: IdlingResource.ResourceCallback? = null
    private var counter: Int = 0

    override fun getName() = name

    override fun isIdleNow() = counter <= 0

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.resourceCallback = callback!!
    }

    fun incrementCounter() = counter++

    fun decrementCounter() {
        counter--

        if (counter == 0) {
            // we've gone from non-zero to zero. That means we're idle now! Tell espresso.
            resourceCallback?.onTransitionToIdle()
        }
    }
}