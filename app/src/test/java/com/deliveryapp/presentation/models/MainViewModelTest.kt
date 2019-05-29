package com.deliveryapp.presentation.models

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.deliveryapp.presentation.main.DeliveryBoundaryCallback
import com.deliveryapp.presentation.main.MainRouter
import com.deliveryapp.presentation.main.MainViewModel
import com.deliveryapp.rx.RxJavaTestHooksResetRule
import com.deliveryapp.domain.entity.Delivery
import com.deliveryapp.domain.repository.State
import com.deliveryapp.domain.usecase.DeliveryUseCase
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

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

    @Mock
    lateinit var mainRouter: MainRouter

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setUp() {
        deliveryUseCase = Mockito.mock(DeliveryUseCase::class.java)
        deliveryBoundaryCallback = DeliveryBoundaryCallback(deliveryUseCase, CompositeDisposable())
        given(deliveryUseCase.getDeliveries()).willReturn(mock())
        mainViewModel = MainViewModel(deliveryUseCase, mainRouter)
        mainViewModel.deliveryBoundaryCallback = deliveryBoundaryCallback
    }

    @Test
    fun deliveryListTest() {
        given(deliveryUseCase.getDeliveries()).willReturn(mock())
        deliveryUseCase.removeAll()
        mainViewModel = MainViewModel(deliveryUseCase, mainRouter)
        mainViewModel.deliveryBoundaryCallback.state.value?.let { assert(it.equals(State.LOADING)) }

        assert(mainViewModel.deliveryList.value?.size != 0)
        mainViewModel.deliveryBoundaryCallback.state.value?.let { assert(it.equals(State.DONE)) }
    }

    @Test
    fun deliveryListLocalTest() {
        mainViewModel =
            MainViewModel(deliveryUseCase, mainRouter)
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


    @Test
    fun onDeliveryClickedTest() {
        val delivery = mock<Delivery>()
        mainViewModel.onDeliveryClicked(delivery)
        verify(mainRouter).navigate(eq(MainRouter.Route.DELIVERY_DETAIL), any())

        mainRouter.navigate(eq(MainRouter.Route.IMAGE_DETAIL), any())
        verify(mainRouter, never()).showNextScreen(any(), any())
    }

    @Test
    fun getStateTest() {
        mainViewModel.deliveryBoundaryCallback.state.value?.let { assert(it.equals(State.DONE)) }
    }
}