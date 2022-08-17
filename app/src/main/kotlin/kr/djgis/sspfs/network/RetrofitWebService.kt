/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.network

import kr.djgis.sspfs.data.Result
import kr.djgis.sspfs.data.*
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
    ): Call<FeatureList>

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
    fun districtGet(
        @Query("xmin") xmin: Double,
        @Query("ymin") ymin: Double,
        @Query("xmax") xmax: Double,
        @Query("ymax") ymax: Double,
    ): Call<DistrictList>

    @GET("api/theme")
    fun themeGet(
        @Query("xmin") xmin: Double,
        @Query("ymin") ymin: Double,
        @Query("xmax") xmax: Double,
        @Query("ymax") ymax: Double,
        @Query("name") name: String,
    ): Call<ThemeList>

    @GET("api/mobile/update")
    fun mobileUpdate(
        @Query("version") version: String,
    ): Call<Version>

    @Multipart
    @POST("api/feature")
    fun featurePost(
        @Part jsonBody: MultipartBody.Part,
        @Query("edit") edit: String?,
        @Query("fraction") fraction: Double?,
        @Part multipartBody: List<MultipartBody.Part?>,
    ): Call<Result>

    @POST("api/feature/switch")
    fun featureSwitch(
        @Query("fac_typ") fac_typ: String,
        @Query("fac_uid") fac_uid: String,
    ): Call<Result>

    @POST("api/feature/create/point")
    fun createFeaturePoint(
        @Body feature: FeatureEditViewModel.Feature,
    ): Call<Result>

    @POST("api/feature/create/line")
    fun createFeatureLine(
        @Body feature: FeatureEditViewModel.Feature,
    ): Call<Result>
}
