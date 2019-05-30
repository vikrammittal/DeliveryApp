package com.deliveryapp.presentation

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.deliveryapp.domain.usecase.DeliveryUseCase
import com.deliveryapp.presentation.deliverydetail.DeliveryDetailViewModel
import com.deliveryapp.presentation.main.MainViewModel
import javax.inject.Inject


@Suppress("UNCHECKED_CAST")
class ViewModelFactory @Inject constructor(
    private var deliveryUseCase: DeliveryUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(deliveryUseCase) as T
        } else return if (modelClass.isAssignableFrom(DeliveryDetailViewModel::class.java)) {
            DeliveryDetailViewModel(deliveryUseCase) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}