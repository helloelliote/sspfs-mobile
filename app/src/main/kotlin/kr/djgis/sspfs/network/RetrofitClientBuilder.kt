/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.network

import com.google.gson.GsonBuilder
import kr.djgis.sspfs.BuildConfig
import kr.djgis.sspfs.Config.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientBuilder {

    val webService: RetrofitWebService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create())).build()
            .create(RetrofitWebService::class.java)
    }

    val kakaoService: RetrofitKakaoService by lazy {
        Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com")
            .client(okHttpClient())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().serializeNulls().create())).build()
            .create(RetrofitKakaoService::class.java)
    }

    private val okHttpClient = {
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (BuildConfig.DEBUG) BODY else NONE
//        builder.addInterceptor(interceptor)
        builder.build()
    }
}
