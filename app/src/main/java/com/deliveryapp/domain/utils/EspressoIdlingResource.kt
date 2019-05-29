package com.deliveryapp.domain.utils

import android.support.test.espresso.IdlingResource


class EspressoIdlingResource {

    companion object {
        private const val RESOURCE = "GLOBAL"
        private val countingIdlingResource =
            SimpleIdlingResource(RESOURCE)

        fun increment() =
            countingIdlingResource.incrementCounter()

        fun decrement() =
            countingIdlingResource.decrementCounter()

        fun getIdlingResource(): IdlingResource = countingIdlingResource

    }
}