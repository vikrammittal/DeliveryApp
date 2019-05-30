package com.deliveryapp.presentation

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.deliveryapp.BuildConfig
import com.deliveryapp.domain.usecase.DeliveryUseCase
import com.deliveryapp.presentation.main.DeliveryBoundaryCallback
import com.deliveryapp.rx.RxJavaTestHooksResetRule
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito

class DeliveryBoundaryCallbackTest {

    @get:Rule
    var rxJavaTestHooksResetRule = RxJavaTestHooksResetRule()
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var deliveryUseCase: DeliveryUseCase
    lateinit var deliveryBoundaryCallback: DeliveryBoundaryCallback

    @Before
    fun setUp() {
        deliveryUseCase = Mockito.mock(DeliveryUseCase::class.java)
        deliveryBoundaryCallback = DeliveryBoundaryCallback(deliveryUseCase, CompositeDisposable())
    }

    @Test
    fun initLoadTest() {
        given(deliveryUseCase.fetchDeliveries(any(), any())).willReturn(Observable.just(mock()))

        val spy = spy(deliveryBoundaryCallback)
        spy.onZeroItemsLoaded()
        verify(spy, times(1)).fetchNetwork(0, BuildConfig.PAGE_SIZE)
        verify(spy, times(1)).updateState(any())
    }

    @Test
    fun endLoadTest() {
        given(deliveryUseCase.fetchDeliveries(any(), any())).willReturn(Observable.just(mock()))

        val spy = spy(deliveryBoundaryCallback)
        spy.onItemAtEndLoaded(mock())
        verify(spy, times(1)).fetchNetwork(any(), any())
        verify(spy, times(1)).updateState(any())
    }

    @Test
    fun refreshTest() {
        given(deliveryUseCase.fetchDeliveries(any(), any())).willReturn(Observable.just(mock()))

        val spy = spy(deliveryBoundaryCallback)
        spy.onRefresh()
        assert(spy.totalCount == 0)
        verify(spy, times(1)).onZeroItemsLoaded()
        verify(spy, times(1)).fetchNetwork(0, BuildConfig.PAGE_SIZE)
        verify(spy, times(1)).updateState(any())
    }
}