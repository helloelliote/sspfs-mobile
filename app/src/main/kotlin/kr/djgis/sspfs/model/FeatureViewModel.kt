/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.model

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.djgis.sspfs.data.Feature
import kr.djgis.sspfs.data.FeaturePK4
import kr.djgis.sspfs.network.Moshi
import kr.djgis.sspfs.network.Moshi.moshiFeatureList
import kr.djgis.sspfs.network.RetrofitClient

class FeatureViewModel : ViewModel() {

    private val retrofit = RetrofitClient

    private val _feature = MutableLiveData<Feature>()
    val feature: LiveData<Feature> = _feature

    fun setFeature(feature: Feature) {
        _feature.value = feature
    }

    fun features(xmin: Double, ymin: Double, xmax: Double, ymax: Double) = liveData {
        withContext(Dispatchers.IO) {
            val jsonObject = retrofit.featuresGet(xmin, ymin, xmax, ymax)
            val featureList = moshiFeatureList.fromJson(jsonObject.toString())
            emit(featureList!!)
        }
    }

    fun feature(id: String) = liveData {
        withContext(Dispatchers.IO) {
            val jsonObject = retrofit.featureGet(id)
            val featureList = moshiFeatureList.fromJson(jsonObject.toString())
            emit(featureList)
        }
    }

    fun fromPK4(pk4: String) = FeaturePK4.valueOf(pk4).type

    suspend fun fromLatLng(feature: Feature): String? {
        val latLng = feature.geom.latLngs[0][0] // FIXME: 시점이 아닌 중점으로 검색할 수 있는 방법
        val jsonObject = retrofit.kakaoCoord2Address(latLng.longitude, latLng.latitude)
        val coord2Address = Moshi.moshiCoord2Address.fromJson(jsonObject.toString())
        return coord2Address?.let { (documents, meta) ->
            when (meta.total_count) {
                1 -> documents[0].address.address_name
                else -> "주소 정보 없음"
            }
        }
    }
}

object FeatureVMFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST") return FeatureViewModel() as T
    }
}
