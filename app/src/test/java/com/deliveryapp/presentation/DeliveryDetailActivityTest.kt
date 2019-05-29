package com.deliveryapp.presentation

import android.databinding.ObservableField
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.deliveryapp.R
import com.deliveryapp.domain.entity.Delivery
import com.deliveryapp.domain.entity.DeliveryLocation
import com.deliveryapp.presentation.deliverydetail.DeliveryDetailActivity
import com.deliveryapp.presentation.deliverydetail.DeliveryDetailViewModel
import com.nhaarman.mockito_kotlin.given
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DeliveryDetailActivityTest {

    private lateinit var activity: DeliveryDetailActivity
    @Mock
    private lateinit var viewModel: DeliveryDetailViewModel

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(DeliveryDetailActivity::class.java)
            .create()
            .resume()
            .get()

        viewModel = Mockito.mock(DeliveryDetailViewModel::class.java)
    }

    @Test
    fun viewTest() {
        val delivery = Delivery(
            1,
            "hi",
            "",
            DeliveryLocation( 1.1, 1.1, "")
        )
        given(viewModel.delivery).willReturn(ObservableField(delivery))
        viewModel.bound(1)
        assert(viewModel.delivery.get()?.description.equals("hi"))
        assert(activity.findViewById<RelativeLayout>(R.id.delivery_details) != null)
        assert(activity.findViewById<RelativeLayout>(R.id.delivery_details).findViewById<TextView>(R.id.description) != null)
        assert(activity.findViewById<RelativeLayout>(R.id.delivery_details).visibility == View.VISIBLE)
        assert(activity.findViewById<RelativeLayout>(R.id.delivery_details).findViewById<TextView>(R.id.description).visibility == View.VISIBLE)
    }

}