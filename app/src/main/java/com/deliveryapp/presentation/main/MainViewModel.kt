package com.deliveryapp.presentation.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.databinding.ObservableBoolean
import android.os.Bundle
import com.deliveryapp.domain.Constants
import com.deliveryapp.domain.entity.Delivery
import com.deliveryapp.domain.usecase.DeliveryUseCase
import com.deliveryapp.presentation.deliverydetail.DeliveryDetailActivity
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MainViewModel @Inject constructor(
    val deliveryUseCase: DeliveryUseCase,
    private val mainRouter: MainRouter
) : ViewModel() {

    private val disposables = CompositeDisposable()
    val isLoading = ObservableBoolean()
    val progressVisible = ObservableBoolean()
    var deliveryList: LiveData<PagedList<Delivery>>
    var deliveryBoundaryCallback: DeliveryBoundaryCallback

    init {
        val config = PagedList.Config.Builder()
            .setPageSize(Constants.PAGE_SIZE)
            .setInitialLoadSizeHint(Constants.PAGE_SIZE)
            .setEnablePlaceholders(false)
            .build()

        deliveryBoundaryCallback =
            DeliveryBoundaryCallback(deliveryUseCase, disposables)
        deliveryList = LivePagedListBuilder<Int, Delivery>(deliveryUseCase.getDeliveries(), config)
            .setBoundaryCallback(deliveryBoundaryCallback).build()
    }

    fun onRefresh() {
        isLoading.set(true)
        deliveryBoundaryCallback.onRefresh()
    }

    // Shows delivery detail screen based on delivery clicked
    fun onDeliveryClicked(delivery: Any) {
        mainRouter.navigate(MainRouter.Route.DELIVERY_DETAIL, Bundle().apply {
            putInt(DeliveryDetailActivity.EXTRA_DELIVERY_ID, (delivery as Delivery).id)
        })
    }

    fun retry() = deliveryBoundaryCallback.retry()

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}