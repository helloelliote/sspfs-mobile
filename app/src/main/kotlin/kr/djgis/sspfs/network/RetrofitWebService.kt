/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.network

import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitWebService {

    @GET("/api/features")
    suspend fun featuresGet(
        @Query("xmin") xmin: Double,
        @Query("ymin") ymin: Double,
        @Query("xmax") xmax: Double,
        @Query("ymax") ymax: Double,
    ): JsonObject

    @GET("/api/feature")
    suspend fun featureGet(
        @Query("id") id: String,
    ): JsonObject
}
