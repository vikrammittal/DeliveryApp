package com.deliveryapp

import android.content.Intent
import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.widget.TextView
import com.deliveryapp.presentation.deliverydetail.DeliveryDetailActivity
import com.deliveryapp.utils.Util
import com.deliveryapp.domain.entity.Delivery
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeliveryDetailsActivityTest {

    @Rule
    @JvmField
    val rule = ActivityTestRule(DeliveryDetailActivity::class.java)

    @Test
    fun viewTest() {
        Espresso.onView(ViewMatchers.withId(R.id.map))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.delivery_details))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.imageView))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        Espresso.onView(ViewMatchers.withId(R.id.description))
            .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }


    @Test
    fun mockDeliveryItemTest() {
        val listType = object : TypeToken<List<Delivery>>() {}.type
        val list: List<Delivery> = Gson().fromJson(Util.loadJson("json/deliveries.json"), listType)
        val activity = rule.launchActivity(Intent())
        activity.viewModel.delivery.set(list.get(0))

        assert(
            activity.findViewById<TextView>(R.id.description).text.equals(
                list.get(0).description
            )
        )
    }
}