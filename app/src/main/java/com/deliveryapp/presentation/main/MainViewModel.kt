package com.deliveryapp.presentation.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import android.databinding.ObservableBoolean
import com.deliveryapp.BuildConfig
import com.deliveryapp.domain.entity.Delivery
import com.deliveryapp.domain.usecase.DeliveryUseCase
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class MainViewModel @Inject constructor(
    var deliveryUseCase: DeliveryUseCase
) : ViewModel() {

    private val disposables = CompositeDisposable()
    val isLoading = ObservableBoolean()
    val progressVisible = ObservableBoolean()
    var deliveryList: LiveData<PagedList<Delivery>>
    var deliveryBoundaryCallback: DeliveryBoundaryCallback

    init {
        val config = PagedList.Config.Builder()
            .setPageSize(BuildConfig.PAGE_SIZE)
            .setInitialLoadSizeHint(BuildConfig.PAGE_SIZE)
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

    fun retry() = deliveryBoundaryCallback.retry()

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}