/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.network

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kr.djgis.sspfs.data.Feature
import retrofit2.http.*

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
        @Query("fac_uid") fac_uid: String,
    ): JsonObject

    @POST("/api/feature/{fac_uid}")
    suspend fun featurePost(
        @Path(value = "fac_uid", encoded = true) fac_uid: String,
        @Body requestBody: Feature,
    ): JsonElement
}
