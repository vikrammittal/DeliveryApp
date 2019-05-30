package com.deliveryapp.presentation.models

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.deliveryapp.domain.repository.State
import com.deliveryapp.domain.usecase.DeliveryUseCase
import com.deliveryapp.presentation.main.DeliveryBoundaryCallback
import com.deliveryapp.presentation.main.MainViewModel
import com.deliveryapp.rx.RxJavaTestHooksResetRule
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.net.UnknownHostException

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @get:Rule
    var rxJavaTestHooksResetRule = RxJavaTestHooksResetRule()
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var deliveryUseCase: DeliveryUseCase
    @Mock
    lateinit var deliveryBoundaryCallback: DeliveryBoundaryCallback

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        deliveryUseCase = Mockito.mock(DeliveryUseCase::class.java)
        deliveryBoundaryCallback = DeliveryBoundaryCallback(deliveryUseCase, CompositeDisposable())
        given(deliveryUseCase.getDeliveries()).willReturn(mock())
        mainViewModel = MainViewModel(deliveryUseCase)
        mainViewModel.deliveryBoundaryCallback = deliveryBoundaryCallback
    }

    @Test
    fun deliveryListTest() {
        given(deliveryUseCase.getDeliveries()).willReturn(mock())
        deliveryUseCase.removeAll()
        mainViewModel = MainViewModel(deliveryUseCase)

        assert(mainViewModel.deliveryList.value?.size != 0)
        mainViewModel.deliveryBoundaryCallback.state.value?.let { assert(it.equals(State.DONE)) }
    }

    @Test(expected = IllegalStateException::class)
    fun apiFailTest() {
        given(deliveryUseCase.fetchDeliveries(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).willThrow(
            IllegalStateException()
        )
        deliveryBoundaryCallback.onZeroItemsLoaded()
    }

    @Test
    fun apiErrorTest() {
        given(deliveryUseCase.fetchDeliveries(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).willReturn(
            Observable.error(Throwable())
        )
        val observer = mock<Observer<String>>()
        mainViewModel.deliveryBoundaryCallback.state.observeForever(observer)
        mainViewModel.deliveryBoundaryCallback.onZeroItemsLoaded()
        val inOrder = Mockito.inOrder(observer)
        inOrder.verify(observer).onChanged(State.LOADING)
        inOrder.verify(observer).onChanged(State.ERROR)
    }

    @Test
    fun networkErrorTest() {
        given(deliveryUseCase.fetchDeliveries(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).willReturn(
            Observable.error(UnknownHostException())
        )
        val observer = mock<Observer<String>>()
        mainViewModel.deliveryBoundaryCallback.state.observeForever(observer)
        mainViewModel.deliveryBoundaryCallback.onZeroItemsLoaded()
        val inOrder = Mockito.inOrder(observer)
        inOrder.verify(observer).onChanged(State.LOADING)
        inOrder.verify(observer).onChanged(State.NETWORK_ERROR)
    }

    @Test
    fun deliveryListLocalTest() {
        mainViewModel =
            MainViewModel(deliveryUseCase)
        mainViewModel.deliveryBoundaryCallback.state.value?.let { assert(it.equals(State.LOADING)) }

        verify(deliveryUseCase, never()).fetchDeliveries(any(), any())

        assert(mainViewModel.deliveryList.value?.size != 0)
        mainViewModel.deliveryBoundaryCallback.state.value?.let { assert(it.equals(State.DONE)) }
    }

    @Test
    fun onRefreshTest() {
        given(deliveryUseCase.fetchDeliveries(any(), any())).willReturn(Observable.just(mock()))

        assert(!mainViewModel.isLoading.get())
        mainViewModel.onRefresh()

        val spy = spy(mainViewModel.deliveryBoundaryCallback)
        assert(mainViewModel.isLoading.get())

        spy.onRefresh()
        verify(spy, times(1)).onZeroItemsLoaded()
    }
}