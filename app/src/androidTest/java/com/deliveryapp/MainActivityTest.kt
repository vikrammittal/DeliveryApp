package com.deliveryapp

import android.arch.persistence.room.Room
import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.swipeDown
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnHolderItem
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToHolder
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import com.deliveryapp.data.repository.local.Database
import com.deliveryapp.domain.utils.EspressoIdlingResource
import com.deliveryapp.presentation.deliverydetail.DeliveryDetailActivity
import com.deliveryapp.presentation.main.MainActivity
import com.deliveryapp.utils.CustomViewMatchers
import com.deliveryapp.utils.Util
import com.squareup.okhttp.mockwebserver.MockResponse
import com.squareup.okhttp.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    private lateinit var db: Database

    @Rule
    @JvmField
    val rule = ActivityTestRule(MainActivity::class.java, true, false)

    private lateinit var activity: MainActivity
    private lateinit var mockServer: MockWebServer

    @Before
    fun setUp() {
        Intents.init()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource())

        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), Database::class.java).build()
        db.deliveryDao().removeAll()

        mockServer = MockWebServer()
        mockServer.play(Util.MOCK_PORT)
    }

    @Test
    fun mockDataSuccessTest() {
        initTest()
        mockServer.enqueue(Util.createResponse("json/deliveries.json"))

        assert(activity.adapter.itemCount == 2)
        assert(activity.adapter.getDeliveryItem(0)?.description.equals("Deliver documents to Andrio at Cheung Sha Wan"))

        val matcher = CustomViewMatchers.withTitle("0Deliver documents to Andrio at Cheung Sha Wan")
        onView((withId(R.id.recyclerView))).perform(scrollToHolder(matcher), actionOnHolderItem(matcher, click()))
    }

    @Test
    fun mockApiFailTest() {
        mockServer.enqueue(MockResponse().setResponseCode(400).setBody("Internal server error"))
        activity = rule.launchActivity(Intent())

        assert(activity.adapter.itemCount == 0)

        onView(withText(activity.getString(R.string.delivery_list_error_message)))
            .withFailureHandler { _, _ -> }.check(matches(isDisplayed()))
    }

    @Test
    fun viewTest() {
        initTest()

        Espresso.onView(withId(R.id.progressBar))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
        Espresso.onView(withId(R.id.recyclerView))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(20))
    }

    @Test
    fun clickTest() {
        initTest()

        onView(withId(R.id.recyclerView))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        Intents.intended(IntentMatchers.hasComponent(DeliveryDetailActivity::class.java.name))
    }

    @Test
    fun pullToRefresh_shouldPass() {
        initTest()
        onView(withId(R.id.swipeRefreshLayout)).perform(swipeDown())
    }

    private fun initTest() {
        mockServer.enqueue(Util.createResponse("json/deliveries.json"))
        activity = rule.launchActivity(Intent())

        onView(withText("OK"))
            .withFailureHandler { _, _ -> }.check(matches(isDisplayed())).perform(click())
    }

    @After
    fun tearUp() {
        Intents.release()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource())

        mockServer.shutdown()
    }
}