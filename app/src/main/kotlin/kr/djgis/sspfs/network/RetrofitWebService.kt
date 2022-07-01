/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.network

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kr.djgis.sspfs.data.FeatureA
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitWebService {

    @GET("/api/features")
    suspend fun featuresGet(
        @Query("xmin") xmin: Double,
        @Query("ymin") ymin: Double,
        @Query("xmax") xmax: Double,
        @Query("ymax") ymax: Double,
    ): JsonObject

    @GET("/api/features/a")
    suspend fun featuresAGet(
        @Query("xmin") xmin: Double,
        @Query("ymin") ymin: Double,
        @Query("xmax") xmax: Double,
        @Query("ymax") ymax: Double,
    ): JsonObject

    @POST("/api/feature/a")
    suspend fun featuresAPost(
        @Body requestBody: FeatureA,
    ): Response<JsonElement>

    @GET("/api/feature")
    suspend fun featureGet(
        @Query("fac_uid") fac_uid: String,
    ): JsonObject
}
