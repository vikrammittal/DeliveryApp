package com.deliveryapp.presentation.main

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagedList
import com.deliveryapp.BuildConfig
import com.deliveryapp.domain.entity.Delivery
import com.deliveryapp.domain.entity.Result
import com.deliveryapp.domain.repository.State
import com.deliveryapp.domain.usecase.DeliveryUseCase
import com.deliveryapp.domain.utils.EspressoIdlingResource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.net.UnknownHostException

@Suppress("UNCHECKED_CAST")
class DeliveryBoundaryCallback(
    private var deliveryUseCase: DeliveryUseCase,
    private var disposable: CompositeDisposable
) :
    PagedList.BoundaryCallback<Delivery>() {

    var totalCount: Int = 0
    private var isLoaded: Boolean = false
    var state: MutableLiveData<String> = MutableLiveData()

    override fun onZeroItemsLoaded() {
        updateState(State.LOADING)
        fetchNetwork(0, BuildConfig.PAGE_SIZE)
        EspressoIdlingResource.increment()
    }

    override fun onItemAtFrontLoaded(itemAtFront: Delivery) {
        super.onItemAtFrontLoaded(itemAtFront)

        disposable.add(deliveryUseCase.getCount()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { result ->
                if (totalCount < result) totalCount = result
            })

    }

    override fun onItemAtEndLoaded(itemAtEnd: Delivery) {
        super.onItemAtEndLoaded(itemAtEnd)
        if (!isLoaded) {
            updateState(State.PAGE_LOADING)
            fetchNetwork(totalCount, BuildConfig.PAGE_SIZE)
        }
    }

    fun fetchNetwork(offset: Int, limit: Int) {
        disposable.add(
            deliveryUseCase.fetchDeliveries(offset, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    when (result) {
                        is Result.Success -> {
                            success(result.response as List<Delivery>)
                        }
                        is Result.Failure -> {
                            error(result.throwable)
                        }
                    }
                }, { e -> error(e) })
        )
    }

    private fun success(list: List<Delivery>) {
        totalCount += list.size
        if (list.size < BuildConfig.PAGE_SIZE) {
            isLoaded = true
            updateState(State.LOADED)
        } else
            updateState(State.DONE)
        EspressoIdlingResource.decrement()
    }

    private fun error(throwable: Throwable) {
        if (throwable is UnknownHostException) {
            updateState(State.NETWORK_ERROR)
        } else {
            updateState(State.ERROR)
        }
        EspressoIdlingResource.decrement()
    }

    fun updateState(state: String) {
        this.state.postValue(state)
    }

    fun retry() {
        updateState(State.PAGE_LOADING)
        fetchNetwork(totalCount, BuildConfig.PAGE_SIZE)
    }

    fun onRefresh() {
        totalCount = 0
        isLoaded = false

        disposable.clear()
        onZeroItemsLoaded()
    }
}