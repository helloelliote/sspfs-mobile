/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.model

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.djgis.sspfs.data.FeatureA
import kr.djgis.sspfs.data.FeatureBase
import kr.djgis.sspfs.network.Moshi
import kr.djgis.sspfs.network.RetrofitClient

open class FeatureViewModel2 : ViewModel() {

    private val retrofit = RetrofitClient

    private val _featureA = MutableLiveData<FeatureA>()
    val featureA: LiveData<FeatureA> = _featureA

    fun value(): FeatureA {
        return featureA.value!!
    }

    fun setFeatureA(featureA: FeatureBase) {
        _featureA.value = featureA as FeatureA
    }

    fun featuresA(xmin: Double, ymin: Double, xmax: Double, ymax: Double) = liveData {
        withContext(Dispatchers.IO) {
            val jsonObject = retrofit.featuresAGet(xmin, ymin, xmax, ymax)
            val featureAList = Moshi.moshiFeatureAList.fromJson(jsonObject.toString())
            emit(featureAList!!)
        }
    }

    suspend fun fromLatLng(featureA: FeatureA): String? {
        val latLng = featureA.geom.latLngs[0][0] // FIXME: 시점이 아닌 중점으로 검색할 수 있는 방법
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

object FeatureVMFactory2 : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST") return FeatureViewModel2() as T
    }
}
