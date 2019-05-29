package com.deliveryapp.presentation

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import android.content.DialogInterface
import android.databinding.ObservableBoolean
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.deliveryapp.R
import com.deliveryapp.domain.entity.Delivery
import com.deliveryapp.domain.entity.DeliveryLocation
import com.deliveryapp.domain.repository.State
import com.deliveryapp.domain.usecase.DeliveryUseCase
import com.deliveryapp.presentation.adapter.DeliveryListAdapter
import com.deliveryapp.presentation.deliverydetail.DeliveryDetailActivity
import com.deliveryapp.presentation.main.DeliveryBoundaryCallback
import com.deliveryapp.presentation.main.MainActivity
import com.deliveryapp.presentation.main.MainRouter
import com.deliveryapp.presentation.main.MainViewModel
import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import kotlinx.android.synthetic.main.activity_main.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import java.lang.ref.WeakReference


@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class MainActivityTest {
    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private lateinit var activity: MainActivity
    private lateinit var activityController: ActivityController<MainActivity>

    @Mock
    private lateinit var viewModel: MainViewModel
    @Mock
    private lateinit var deliveryList: MutableLiveData<PagedList<Delivery>>
    @Mock
    private lateinit var state: MutableLiveData<String>
    @Mock
    private lateinit var isLoading: ObservableBoolean
    @Mock
    private lateinit var deliveryBoundaryCallback: DeliveryBoundaryCallback

    @Captor
    private lateinit var deliveryListCaptor: ArgumentCaptor<Observer<PagedList<Delivery>>>
    @Captor
    private lateinit var stateCaptor: ArgumentCaptor<Observer<String>>

    @Before
    fun setUp() {
        given(viewModel.deliveryList).willReturn(deliveryList)
        given(viewModel.deliveryBoundaryCallback).willReturn(deliveryBoundaryCallback)
        given(viewModel.deliveryBoundaryCallback.state).willReturn(state)
        given(viewModel.isLoading).willReturn(isLoading)

        activityController = Robolectric.buildActivity(MainActivity::class.java)
        activity = activityController.get()
        activityController.create()
        activity.setTestViewModel(viewModel)
        activityController.start()

        verify(deliveryList).observe(ArgumentMatchers.any(LifecycleOwner::class.java), deliveryListCaptor.capture())
        verify(state).observe(ArgumentMatchers.any(LifecycleOwner::class.java), stateCaptor.capture())
    }

    @Test
    fun viewTest() {
        assert(!activity.swipeRefreshLayout.isRefreshing)
        assert(activity.progressBar.visibility == View.VISIBLE)
        assert(activity.recyclerView.visibility == View.VISIBLE)
        assert(activity.recyclerView.itemDecorationCount == 1)
        assert(activity.recyclerView.layoutManager is LinearLayoutManager)
        assert(activity.recyclerView.getItemDecorationAt(0) is DividerItemDecoration)


        val deliveryUseCase = Mockito.mock(DeliveryUseCase::class.java)
        given(deliveryUseCase.getDeliveries()).willReturn(mock())

        val mainRouter = MainRouter(WeakReference(activity))
        val viewModel = MainViewModel(deliveryUseCase, mainRouter)
        viewModel.onDeliveryClicked(mock<Delivery>())

        val startedIntent = Shadows.shadowOf(activity).nextStartedActivity
        val shadowIntent = Shadows.shadowOf(startedIntent)
        assertEquals(DeliveryDetailActivity::class.java, shadowIntent.intentClass)
    }

    @Test
    fun displayListTest() {
        val list = ArrayList<Delivery>()
        for (i in 0 until 10) {
            list.add(
                Delivery(
                    i + 1, "hi", "",
                    DeliveryLocation(1.0, 2.0, "")
                )
            )
        }
        val pagedList = mockPagedList(list)

        deliveryListCaptor.value.onChanged(pagedList)
        stateCaptor.value.onChanged(State.DONE)

        assertEquals(pagedList, activity.adapter.currentList)
        assert(!activity.swipeRefreshLayout.isRefreshing)

        assert(activity.adapter.itemCount == 10)
        activity.adapter.currentList?.size?.let { assert(it == list.size) }
        assert(activity.adapter.getDeliveryItem(2)?.id == 3)
    }

    @Test
    fun errorThrownTest() {
        stateCaptor.value.onChanged(State.ERROR)
        assert(!viewModel.isLoading.get())
    }

    @Test
    fun adapterTest() {
        val delivery1 = Delivery(
            1,
            "hi",
            "",
            DeliveryLocation(1.0, 2.0, "")
        )
        val delivery2 = Delivery(
            1,
            "hi",
            "",
            DeliveryLocation(1.0, 2.0, "")
        )
        assert(DeliveryListAdapter.deliveriesDiffCallback.areContentsTheSame(delivery1, delivery2))
        assert(DeliveryListAdapter.deliveriesDiffCallback.areItemsTheSame(delivery1, delivery2))


        val holder = activity.adapter.onCreateViewHolder(activity.recyclerView, 0)
        if (holder is DeliveryListAdapter.DeliveryItemHolder) {
            holder.bind(delivery1)
            assert(holder.delivery == delivery1)
            assert(holder.binding.description.text.isEmpty())

            val list = ArrayList<Delivery>()
            list.add(delivery1)
            list.add(delivery2)
            activity.adapter.currentList?.add(delivery1)
            activity.adapter.currentList?.add(delivery2)
            holder.bind(delivery1)
            assert(holder.binding.viewModel == delivery1)
        }
    }

    @Test
    fun adapterProgessHolderTest() {
        val holder = activity.adapter.onCreateViewHolder(activity.recyclerView, 1)
        if (holder is DeliveryListAdapter.ProgressViewHolder) {
            holder.bind(true, true)
            assert(holder.binding.progressBar.visibility == View.VISIBLE)
            assert(holder.binding.btnLoadMore.visibility == View.VISIBLE)

            activity.adapter.setLoading(true, true)
            activity.adapter.onBindViewHolder(holder, activity.adapter.itemCount - 1)
            assert(holder.binding.loadMore)
            assert(holder.binding.loading)

            activity.adapter.onBindViewHolder(holder, activity.adapter.itemCount - 1, ArrayList())
            assert(holder.binding.loadMore)
            assert(holder.binding.loading)
        }
    }

    @Test
    fun alertTest() {
        val alert = activity.showAlert(R.string.network_error)
        alert.show()
        assert(alert.isShowing)
        alert.getButton(DialogInterface.BUTTON_POSITIVE).performClick()
        assert(!alert.isShowing)
    }

    private fun <T> mockPagedList(list: List<T>): PagedList<T> {
        val pagedList = Mockito.mock(PagedList::class.java) as PagedList<T>
        Mockito.`when`(pagedList.get(ArgumentMatchers.anyInt())).then { invocation ->
            val index = invocation.arguments.first() as Int
            list[index]
        }
        Mockito.`when`(pagedList.size).thenReturn(list.size)
        return pagedList
    }
}