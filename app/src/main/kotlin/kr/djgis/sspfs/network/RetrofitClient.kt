/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.network

import com.google.gson.JsonObject
import kotlin.math.round

object RetrofitClient {

    private val webService: RetrofitWebService = RetrofitClientBuilder.webService
    private val kakaoService: RetrofitKakaoService = RetrofitClientBuilder.kakaoService

    suspend fun featuresGet(xmin: Double, ymin: Double, xmax: Double, ymax: Double): JsonObject {
        val doubles = listOf(xmin, ymin, xmax, ymax).map { round(it * 10e2) / 10e2 }
        return webService.featuresGet(doubles[0], doubles[1], doubles[2], doubles[3])
    }

    suspend fun featureGet(id: String): JsonObject {
        return webService.featureGet(id = id)
    }

    suspend fun kakaoSearchPlaces(query: String, x: Double?, y: Double?): JsonObject {
        return kakaoService.searchKeyword(query = query, x = x, y = y)
    }

    suspend fun kakaoCoord2Regioncode(x: Double, y: Double, input: String?, output: String?): JsonObject {
        return kakaoService.geoCoord2Regioncode(x = x, y = y, input = input, output = output)
    }

    suspend fun kakaoCoord2Address(x: Double, y: Double, input: String? = null): JsonObject {
        return kakaoService.geoCoord2Address(x = x, y = y, input = input)
    }
}
