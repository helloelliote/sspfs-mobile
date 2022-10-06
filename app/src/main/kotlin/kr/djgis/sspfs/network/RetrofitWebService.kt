/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.network

import kr.djgis.sspfs.data.*
import kr.djgis.sspfs.model.FeatureEditViewModel
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface RetrofitWebService {

    @GET("api/features")
    suspend fun featuresGet(
        @Query("xmin") xmin: Double,
        @Query("ymin") ymin: Double,
        @Query("xmax") xmax: Double,
        @Query("ymax") ymax: Double,
    ): Response<FeatureList>

    @GET("api/feature")
    suspend fun featureGetA(
        @Query("fac_uid") fac_uid: String,
    ): FeatureAList

    @GET("api/feature")
    suspend fun featureGetB(
        @Query("fac_uid") fac_uid: String,
    ): FeatureBList

    @GET("api/feature")
    suspend fun featureGetC(
        @Query("fac_uid") fac_uid: String,
    ): FeatureCList

    @GET("api/feature")
    suspend fun featureGetD(
        @Query("fac_uid") fac_uid: String,
    ): FeatureDList

    @GET("api/feature")
    suspend fun featureGetE(
        @Query("fac_uid") fac_uid: String,
    ): FeatureEList

    @GET("api/feature")
    suspend fun featureGetF(
        @Query("fac_uid") fac_uid: String,
    ): FeatureFList

    @GET("api/region")
    suspend fun districtGet(
        @Query("xmin") xmin: Double,
        @Query("ymin") ymin: Double,
        @Query("xmax") xmax: Double,
        @Query("ymax") ymax: Double,
    ): Response<DistrictList>

    @GET("api/poi/center")
    suspend fun poiCenterGet(
        @Query("xmin") xmin: Double,
        @Query("ymin") ymin: Double,
        @Query("xmax") xmax: Double,
        @Query("ymax") ymax: Double,
    ): Response<POIList>

    @GET("api/theme")
    suspend fun themeGet(
        @Query("xmin") xmin: Double,
        @Query("ymin") ymin: Double,
        @Query("xmax") xmax: Double,
        @Query("ymax") ymax: Double,
        @Query("name") name: String,
    ): Response<ThemeList>

    @GET("api/mobile/update")
    suspend fun mobileUpdate(
        @Query("version") version: String,
    ): Response<Version>

    @Multipart
    @POST("api/feature")
    suspend fun featurePost(
        @Part jsonBody: MultipartBody.Part,
        @Query("edit") edit: String?,
        @Query("fraction") fraction: Double?,
        @Part multipartBody: List<MultipartBody.Part?>,
    ): Response<Result>

    @POST("api/feature/switch")
    suspend fun featureSwitch(
        @Query("fac_typ") fac_typ: String,
        @Query("fac_uid") fac_uid: String,
    ): Response<Result>

    @POST("api/feature/create/point")
    suspend fun createFeaturePoint(
        @Body feature: FeatureEditViewModel.Feature,
    ): Response<Result>

    @POST("api/feature/create/line")
    suspend fun createFeatureLine(
        @Body feature: FeatureEditViewModel.Feature,
    ): Response<Result>
}
