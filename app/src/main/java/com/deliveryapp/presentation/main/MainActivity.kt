package com.deliveryapp.presentation.main

import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import com.deliveryapp.R
import com.deliveryapp.databinding.ActivityMainBinding
import com.deliveryapp.domain.repository.State
import com.deliveryapp.presentation.BaseActivity
import com.deliveryapp.presentation.ViewModelFactory
import com.deliveryapp.presentation.adapter.DeliveryListAdapter
import org.jetbrains.annotations.TestOnly
import javax.inject.Inject


class MainActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    lateinit var viewModel: MainViewModel
    lateinit var adapter: DeliveryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        screenComponent.inject(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        binding.viewModel = viewModel

        adapter = DeliveryListAdapter()
        adapter.onItemClickListener = { viewModel.onDeliveryClicked(it) }
        adapter.onLoadMoreClickListener = { viewModel.retry() }
        binding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                applicationContext,
                DividerItemDecoration.VERTICAL
            )
        )

        observerViewModel()
    }

    private fun observerViewModel(){
        viewModel.deliveryList.observe(this, Observer {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })

        viewModel.deliveryBoundaryCallback.state.observe(this, Observer {
            when (it) {
                State.ERROR, State.NETWORK_ERROR -> {
                    viewModel.isLoading.set(it == State.DONE)
                    adapter.setLoading(false, adapter.itemCount != 0)
                    showAlert(
                        if (it == State.NETWORK_ERROR) {
                            R.string.network_error
                        } else R.string.delivery_list_error_message
                    ).show()
                    adapter.notifyItemChanged(adapter.itemCount - 1)
                }
                State.PAGE_LOADING -> {
                    adapter.setLoading(loading = true, loadMore = false)
                    adapter.notifyItemChanged(adapter.itemCount - 1)
                }
                State.LOADED -> {
                    adapter.setLoading(loading = false, loadMore = false)
                    viewModel.isLoading.set(it == State.LOADING)
                    showAlert(R.string.all_data_fetched).show()
                    adapter.notifyDataSetChanged()
                }
                else -> {
                    adapter.setLoading(loading = false, loadMore = false)
                    viewModel.isLoading.set(it == State.LOADING)
                }
            }
        })
    }

    fun showAlert(error: Int): AlertDialog = AlertDialog.Builder(this)
        .setMessage(getString(error))
        .setNeutralButton(getString(android.R.string.ok)) { dialog, _ -> dialog.dismiss() }
        .create()

    @TestOnly
    fun setTestViewModel(viewModel: MainViewModel) {
        this.viewModel = viewModel
        observerViewModel()
    }
}
