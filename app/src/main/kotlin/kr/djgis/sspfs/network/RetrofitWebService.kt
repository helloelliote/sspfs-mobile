/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.network

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kr.djgis.sspfs.model.FeatureEditViewModel.Feature
import okhttp3.MultipartBody
import retrofit2.http.*

interface RetrofitWebService {

    @GET("api/features")
    suspend fun featuresGet(
        @Query("xmin") xmin: Double,
        @Query("ymin") ymin: Double,
        @Query("xmax") xmax: Double,
        @Query("ymax") ymax: Double,
    ): JsonObject

    @GET("api/feature")
    suspend fun featureGet(
        @Query("fac_uid") fac_uid: String,
    ): JsonObject

    @Multipart
    @POST("api/feature")
    suspend fun featurePost(
        @Part jsonBody: MultipartBody.Part,
        @Query("edit") edit: String?,
        @Query("fraction") fraction: Double?,
        @Part multipartBody: List<MultipartBody.Part?>,
    ): JsonElement

    @POST("api/feature/create/point")
    suspend fun createFeaturePoint(
        @Body feature: Feature,
    ): JsonObject

    @POST("api/feature/create/line")
    suspend fun createFeatureLine(
        @Body feature: Feature,
    ): JsonObject

    @GET("api/region")
    suspend fun districtGet(
        @Query("xmin") xmin: Double,
        @Query("ymin") ymin: Double,
        @Query("xmax") xmax: Double,
        @Query("ymax") ymax: Double,
    ): JsonObject

    @GET("api/theme")
    suspend fun themeGet(
        @Query("xmin") xmin: Double,
        @Query("ymin") ymin: Double,
        @Query("xmax") xmax: Double,
        @Query("ymax") ymax: Double,
        @Query("name") name: String,
    ): JsonObject
}
