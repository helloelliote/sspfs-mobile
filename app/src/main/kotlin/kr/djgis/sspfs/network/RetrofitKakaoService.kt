/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.network

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface RetrofitKakaoService {

    /**
     * @link https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-keyword
     */
    @Headers(KAKAO_API_KEY)
    @GET("/v2/local/search/keyword.$FORMAT")
    fun searchKeyword(
        @Query("query") query: String,
        @Query("x") x: Double?,
        @Query("y") y: Double?,
    ): Call<JsonObject>

/*    *//**
     * @link https://developers.kakao.com/docs/latest/ko/local/dev-guide#coord-to-district
     *//*
    @Headers(KAKAO_API_KEY)
    @GET("/v2/local/geo/coord2regioncode.$FORMAT")
    fun geoCoord2Regioncode(
        @Query("x") x: Double?,
        @Query("y") y: Double?,
        @Query("input_coord") input: String?,
        @Query("output_coord") output: String?,
    ): Call<JsonObject>

    *//**
     * @link https://developers.kakao.com/docs/latest/ko/local/dev-guide#coord-to-address
     *//*
    @Headers(KAKAO_API_KEY)
    @GET("/v2/local/geo/coord2address.$FORMAT")
    fun geoCoord2Address(
        @Query("x") x: Double?,
        @Query("y") y: Double?,
        @Query("input_coord") input: String?,
    ): Call<JsonObject>*/

    companion object {
        private const val KAKAO_API_KEY = "Authorization: KakaoAK f389a3003f035270af558e04db3fa9f2"
        private const val FORMAT = "json"
    }
}
