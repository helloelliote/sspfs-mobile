/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.network

import com.google.gson.JsonObject
import kr.djgis.sspfs.model.FeatureEditViewModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitWebService {

    @GET("api/features")
    fun featuresGet(
        @Query("xmin") xmin: Double,
        @Query("ymin") ymin: Double,
        @Query("xmax") xmax: Double,
        @Query("ymax") ymax: Double,
    ): Call<JsonObject>

    @GET("api/feature")
    fun featureGet(
        @Query("fac_uid") fac_uid: String,
    ): Call<JsonObject>

    @Multipart
    @POST("api/feature")
    fun featurePost(
        @Part jsonBody: MultipartBody.Part,
        @Query("edit") edit: String?,
        @Query("fraction") fraction: Double?,
        @Part multipartBody: List<MultipartBody.Part?>,
    ): Call<JsonObject>

    @POST("api/feature/switch")
    fun featureSwitch(
        @Query("fac_typ") fac_typ: String,
        @Query("fac_uid") fac_uid: String,
    ): Call<JsonObject>

    @POST("api/feature/create/point")
    fun createFeaturePoint(
        @Body feature: FeatureEditViewModel.Feature,
    ): Call<JsonObject>

    @POST("api/feature/create/line")
    fun createFeatureLine(
        @Body feature: FeatureEditViewModel.Feature,
    ): Call<JsonObject>

    @GET("api/region")
    fun districtGet(
        @Query("xmin") xmin: Double,
        @Query("ymin") ymin: Double,
        @Query("xmax") xmax: Double,
        @Query("ymax") ymax: Double,
    ): Call<JsonObject>

    @GET("api/theme")
    fun themeGet(
        @Query("xmin") xmin: Double,
        @Query("ymin") ymin: Double,
        @Query("xmax") xmax: Double,
        @Query("ymax") ymax: Double,
        @Query("name") name: String,
    ): Call<JsonObject>
}
