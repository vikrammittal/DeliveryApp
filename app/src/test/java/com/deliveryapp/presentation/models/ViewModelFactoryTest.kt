package com.deliveryapp.presentation.models

import android.arch.lifecycle.ViewModel
import com.deliveryapp.domain.usecase.DeliveryUseCase
import com.deliveryapp.presentation.ViewModelFactory
import com.deliveryapp.presentation.deliverydetail.DeliveryDetailViewModel
import com.deliveryapp.presentation.main.MainViewModel
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ViewModelFactoryTest {

    @Mock
    lateinit var deliveryUseCase: DeliveryUseCase

    lateinit var viewModelFactory: ViewModelFactory

    @Before
    fun setUp() {
        viewModelFactory = ViewModelFactory(deliveryUseCase)
    }

    @Test
    fun mainViewModelTest() {
        given(deliveryUseCase.getDeliveries()).willReturn(mock())
        val model: Any = viewModelFactory.create(MainViewModel::class.java)
        assert(model is MainViewModel)
    }

    @Test
    fun detailViewModelTest() {
        val model: Any = viewModelFactory.create(DeliveryDetailViewModel::class.java)
        assert(model is DeliveryDetailViewModel)
    }

    @Test(expected = IllegalArgumentException::class)
    fun unkownViewModelTest() {
        val model: Any = viewModelFactory.create(ViewModel::class.java)
        assert(model is MainViewModel)
    }
}