package com.deliveryapp.di

import com.deliveryapp.data.DeliveryEndpoint
import com.google.gson.Gson
import com.squareup.okhttp.mockwebserver.MockWebServer
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import okhttp3.logging.HttpLoggingInterceptor



@Module
class TestEndpointModule() {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val server = MockWebServer()
        server.play(8080)

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl("http://localhost:8888/")
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideDeliveryEndpoint(retrofit: Retrofit): DeliveryEndpoint {
        return retrofit
            .create(DeliveryEndpoint::class.java)
    }
}