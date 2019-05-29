package com.deliveryapp.utils

import android.support.test.espresso.matcher.BoundedMatcher
import android.support.v7.widget.RecyclerView
import com.deliveryapp.R
import com.deliveryapp.presentation.adapter.DeliveryListAdapter
import org.hamcrest.Description
import org.hamcrest.Matcher


class CustomViewMatchers {

    companion object {
        fun withTitle(title: String): Matcher<RecyclerView.ViewHolder> {
            return object :
                BoundedMatcher<RecyclerView.ViewHolder, DeliveryListAdapter.DeliveryItemHolder>(DeliveryListAdapter.DeliveryItemHolder::class.java) {
                override fun matchesSafely(item: DeliveryListAdapter.DeliveryItemHolder): Boolean {
                    return (item.delivery.id.toString().plus(String.format(
                        item.binding.description.context.getString(R.string.label_delivery),
                        item.delivery.description,
                        item.delivery.location.address
                    )) == title)
                }

                override fun describeTo(description: Description) {
                    description.appendText("view holder with description: $title")
                }
            }
        }
    }

}