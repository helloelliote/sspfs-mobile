/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kr.djgis.sspfs.data.FeatureJsonAdapter
import kr.djgis.sspfs.data.FeatureList
import kr.djgis.sspfs.data.kakao.geo.Coord2Address
import kr.djgis.sspfs.data.kakao.geo.Coord2Regioncode
import kr.djgis.sspfs.data.kakao.search.Keyword

object Moshi {

    private val moshiBuilder: Moshi =
        Moshi.Builder().add(FeatureJsonAdapter()).addLast(KotlinJsonAdapterFactory()).build()

    val moshiKeyword: JsonAdapter<Keyword> by lazy {
        moshiBuilder.adapter(Keyword::class.java).serializeNulls()
    }

    val moshiCoord2Regioncode: JsonAdapter<Coord2Regioncode> by lazy {
        moshiBuilder.adapter(Coord2Regioncode::class.java).serializeNulls()
    }

    val moshiCoord2Address: JsonAdapter<Coord2Address> by lazy {
        moshiBuilder.adapter(Coord2Address::class.java).serializeNulls()
    }

    val moshiFeatureList: JsonAdapter<FeatureList> by lazy {
        moshiBuilder.adapter(FeatureList::class.java).serializeNulls()
    }
}
