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
import com.deliveryapp.presentation.main.MainViewModel
import com.nhaarman.mockito_kotlin.*
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
import org.robolectric.shadows.ShadowAlertDialog


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
    @Mock
    private lateinit var adapter: DeliveryListAdapter

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
        activity.adapter = adapter

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

        activity.onDeliveryClicked(mock<Delivery>())

        val startedIntent = Shadows.shadowOf(activity).nextStartedActivity
        val shadowIntent = Shadows.shadowOf(startedIntent)
        assertEquals(DeliveryDetailActivity::class.java, shadowIntent.intentClass)
    }

    @Test
    fun dataSuccessTest() {
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
        verify(activity.adapter).submitList(pagedList)
        verify(activity.adapter, times(1)).setLoading(any(), any())
    }

    @Test
    fun dataErrorTest() {
        stateCaptor.value.onChanged(State.ERROR)
        assert(ShadowAlertDialog.getShownDialogs().size == 1)
        assert(!viewModel.isLoading.get())
        verify(activity.adapter, times(2)).setLoading(any(), any())
    }

    @Test
    fun networkErrorTest() {
        stateCaptor.value.onChanged(State.NETWORK_ERROR)
        assert(ShadowAlertDialog.getShownDialogs().size == 1)
        assert(!viewModel.isLoading.get())
        verify(activity.adapter, times(2)).setLoading(any(), any())
    }

    @Test
    fun allDataLoadedTest() {
        stateCaptor.value.onChanged(State.LOADED)
        assert(ShadowAlertDialog.getShownDialogs().size == 1)
        assert(!viewModel.isLoading.get())
        verify(activity.adapter, times(2)).setLoading(any(), any())
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

        val pagedList = ArrayList<Delivery>()
        pagedList.add(delivery1)
        pagedList.add(delivery2)

        val adapter = DeliveryListAdapter()
        adapter.onItemClickListener = mock()
        adapter.onLoadMoreClickListener = mock()
        adapter.submitList(mockPagedList(pagedList))
        val holder = adapter.onCreateViewHolder(activity.recyclerView, 0)
        if (holder is DeliveryListAdapter.DeliveryItemHolder) {
            holder.bind(delivery1)
            assert(holder.delivery == delivery1)
            assert(holder.binding.description.text.isEmpty())

            val list = ArrayList<Delivery>()
            list.add(delivery1)
            list.add(delivery2)
            adapter.currentList?.add(delivery1)
            adapter.currentList?.add(delivery2)
            holder.bind(delivery1)
            assert(holder.binding.viewModel == delivery1)
        }
        assert(adapter.getDeliveryItem(0) == delivery1)
    }

    @Test
    fun adapterProgressHolderTest() {
        val delivery1 = Delivery(
            1, "hi", "",
            DeliveryLocation(1.0, 2.0, "")
        )
        val pagedList = ArrayList<Delivery>()
        pagedList.add(delivery1)
        val adapter = DeliveryListAdapter()
        adapter.onItemClickListener = mock()
        adapter.onLoadMoreClickListener = mock()
        adapter.submitList(mockPagedList(pagedList))
        val holder = adapter.onCreateViewHolder(activity.recyclerView, 1)
        if (holder is DeliveryListAdapter.ProgressViewHolder) {
            holder.bind(true, true)
            assert(holder.binding.progressBar.visibility == View.VISIBLE)
            assert(holder.binding.btnLoadMore.visibility == View.VISIBLE)

            adapter.setLoading(true, true)
            adapter.onBindViewHolder(holder, adapter.itemCount - 1)
            assert(holder.binding.loadMore)
            assert(holder.binding.loading)

            adapter.onBindViewHolder(holder, adapter.itemCount - 1, ArrayList())
            assert(holder.binding.loadMore)
            assert(holder.binding.loading)
        }
    }

    @Test
    fun viewModelFactoryTest() {
        val model: Any = activity.viewModelFactory.create(MainViewModel::class.java)
        assert(model is MainViewModel)
    }

    @Test
    fun showAlertTest() {
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