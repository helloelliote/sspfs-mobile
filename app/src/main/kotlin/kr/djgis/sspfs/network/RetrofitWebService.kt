/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.network

import com.google.gson.JsonElement
import com.google.gson.JsonObject
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
    @POST("api/feature/{fac_uid}")
    suspend fun featurePost(
        @Path(value = "fac_uid", encoded = true) fac_uid: String,
        @Part jsonBody: MultipartBody.Part,
        @Part multipartBody: List<MultipartBody.Part?>,
    ): JsonElement

    @GET("api/region")
    suspend fun regionsGet(
        @Query("xmin") xmin: Double,
        @Query("ymin") ymin: Double,
        @Query("xmax") xmax: Double,
        @Query("ymax") ymax: Double,
    ): JsonObject
}
