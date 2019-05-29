package com.deliveryapp.presentation.deliverydetail

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import com.deliveryapp.domain.entity.Delivery
import com.deliveryapp.domain.usecase.DeliveryUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DeliveryDetailViewModel @Inject constructor(
    private var deliveryUseCase: DeliveryUseCase
) : ViewModel() {

    var disposables = CompositeDisposable()
    var delivery = ObservableField<Delivery>()

    fun bound(id: Int) {
        disposables.add(
            deliveryUseCase
                .getDelivery(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    delivery.set(result)
                }
        )
    }

    override fun onCleared() {
        super.onCleared()

        disposables.dispose()
    }
}