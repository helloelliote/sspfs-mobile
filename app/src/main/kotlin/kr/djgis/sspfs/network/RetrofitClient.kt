/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.network

import kr.djgis.sspfs.BuildConfig
import kr.djgis.sspfs.Config
import kr.djgis.sspfs.network.MoshiBuilder.moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    val webService: RetrofitWebService by lazy {
        Retrofit.Builder()
            .baseUrl(Config.BASE_URL)
            .client(okHttpClient())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(RetrofitWebService::class.java)
    }

    val kakaoService: RetrofitKakaoService by lazy {
        Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com")
            .client(okHttpClient())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(RetrofitKakaoService::class.java)
    }

    private val okHttpClient = {
        val builder = OkHttpClient.Builder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.level =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
//        builder.addInterceptor(interceptor)
//        builder.apply {
//            connectTimeout(300, TimeUnit.SECONDS)
//            readTimeout(300, TimeUnit.SECONDS)
//            writeTimeout(300, TimeUnit.SECONDS)
//        }
        builder.build()
    }
}
