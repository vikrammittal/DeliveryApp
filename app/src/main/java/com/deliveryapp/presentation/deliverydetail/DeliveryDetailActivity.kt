package com.deliveryapp.presentation.deliverydetail

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.MenuItem
import com.deliveryapp.R
import com.deliveryapp.databinding.ActivityDeliveryDetailsBinding
import com.deliveryapp.presentation.BaseActivity
import com.deliveryapp.presentation.ViewModelFactory
import javax.inject.Inject


class DeliveryDetailActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var viewModel: DeliveryDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityDeliveryDetailsBinding =
            DataBindingUtil.setContentView(
                this,
                R.layout.activity_delivery_details
            )
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        screenComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(DeliveryDetailViewModel::class.java)
        viewModel.bound(this.intent.getIntExtra(EXTRA_DELIVERY_ID, 0))
        binding.viewModel = viewModel
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_DELIVERY_ID = "delivery"
    }
}