package com.deliveryapp.presentation

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.deliveryapp.presentation.deliverydetail.DeliveryDetailViewModel
import com.deliveryapp.presentation.main.MainRouter
import com.deliveryapp.presentation.main.MainViewModel
import com.deliveryapp.domain.usecase.DeliveryUseCase
import javax.inject.Inject


@Suppress("UNCHECKED_CAST")
class ViewModelFactory @Inject constructor(
    private var deliveryUseCase: DeliveryUseCase,
    private var mainRouter: MainRouter
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            MainViewModel(deliveryUseCase, mainRouter) as T
        } else return if (modelClass.isAssignableFrom(DeliveryDetailViewModel::class.java)) {
            DeliveryDetailViewModel(deliveryUseCase) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}