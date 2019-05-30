package com.deliveryapp.presentation.models

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.deliveryapp.domain.entity.Delivery
import com.deliveryapp.domain.entity.DeliveryLocation
import com.deliveryapp.domain.usecase.DeliveryUseCase
import com.deliveryapp.presentation.deliverydetail.DeliveryDetailViewModel
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class DeliveryDetailViewModelTest {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var deliveryUseCase: DeliveryUseCase

    lateinit var viewModel: DeliveryDetailViewModel

    @Before
    fun setUp() {
        viewModel = DeliveryDetailViewModel(deliveryUseCase)
    }

    @Test
    fun boundTest() {
        val delivery = Delivery(
            1,
            "hi",
            "",
            DeliveryLocation(1.1, 1.1, "")
        )
        given(deliveryUseCase.getDelivery(ArgumentMatchers.anyInt()))
            .willReturn(Single.just(delivery))

        assert(viewModel.disposables.size() == 0)
        assert(viewModel.delivery.get() == null)
        viewModel.bound(ArgumentMatchers.anyInt())
        assert(viewModel.disposables.size() == 1)
        verify(deliveryUseCase, never()).removeAll()
        verify(deliveryUseCase, never()).getCount()
        verify(deliveryUseCase, never()).getDeliveries()
        verify(deliveryUseCase, never()).fetchDeliveries(any(), any())
        deliveryUseCase.getDelivery(1).test().assertValue(delivery)

        val deliveryResponse = deliveryUseCase.getDelivery(ArgumentMatchers.anyInt()).blockingGet()
        assert(delivery == deliveryResponse)
    }

    @Test
    fun deliveryTest() {
        val delivery = Delivery(
            1,
            "hi",
            "",
            DeliveryLocation(1.1, 1.1, "")
        )
        viewModel.delivery.set(delivery)
        assert(viewModel.delivery.get() != null)
    }
}