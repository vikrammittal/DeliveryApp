package com.deliveryapp.utils

import android.support.test.InstrumentationRegistry
import com.squareup.okhttp.mockwebserver.MockResponse

object Util {

    const val MOCK_PORT: Int = 8888

    fun loadJson(path: String): String {
        val context = InstrumentationRegistry.getInstrumentation().context
        val stream = context.getResources().getAssets().open(path)
        val reader = stream.bufferedReader()
        val stringBuilder = StringBuilder()
        reader.lines().forEach {
            stringBuilder.append(it)
        }
        return stringBuilder.toString()
    }

    fun createResponse(path: String): MockResponse = MockResponse()
        .setResponseCode(200)
        .setBody(loadJson(path))
}