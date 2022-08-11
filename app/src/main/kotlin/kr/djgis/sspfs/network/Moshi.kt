/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.network

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kr.djgis.sspfs.data.*
import kr.djgis.sspfs.data.kakao.geo.Coord2Address
import kr.djgis.sspfs.data.kakao.geo.Coord2Regioncode
import kr.djgis.sspfs.data.kakao.search.Keyword
import kr.djgis.sspfs.model.FeatureEditViewModel

object Moshi {

    private val moshiBuilder: Moshi =
        Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

    val moshiKeyword: JsonAdapter<Keyword> by lazy {
        moshiBuilder.adapter(Keyword::class.java).serializeNulls()
    }

    val moshiCoord2Regioncode: JsonAdapter<Coord2Regioncode> by lazy {
        moshiBuilder.adapter(Coord2Regioncode::class.java).serializeNulls()
    }

    val moshiCoord2Address: JsonAdapter<Coord2Address> by lazy {
        moshiBuilder.adapter(Coord2Address::class.java).serializeNulls()
    }

    val moshiFeatureList: JsonAdapter<FeatureList> =
        moshiBuilder.adapter(FeatureList::class.java).serializeNulls()

    val moshiFeatureAList: JsonAdapter<FeatureAList> =
        moshiBuilder.adapter(FeatureAList::class.java).serializeNulls()

    val moshiFeatureBList: JsonAdapter<FeatureBList> =
        moshiBuilder.adapter(FeatureBList::class.java).serializeNulls()

    val moshiFeatureCList: JsonAdapter<FeatureCList> =
        moshiBuilder.adapter(FeatureCList::class.java).serializeNulls()

    val moshiFeatureDList: JsonAdapter<FeatureDList> =
        moshiBuilder.adapter(FeatureDList::class.java).serializeNulls()

    val moshiFeatureEList: JsonAdapter<FeatureEList> =
        moshiBuilder.adapter(FeatureEList::class.java).serializeNulls()

    val moshiFeatureFList: JsonAdapter<FeatureFList> =
        moshiBuilder.adapter(FeatureFList::class.java).serializeNulls()

    val moshiFeatureEditList: JsonAdapter<FeatureEditViewModel.FeatureList> =
        moshiBuilder.adapter(FeatureEditViewModel.FeatureList::class.java).serializeNulls()

    val moshiRegionList: JsonAdapter<RegionList> =
        moshiBuilder.adapter(RegionList::class.java).serializeNulls()
}
