/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.model

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.djgis.sspfs.data.*
import kr.djgis.sspfs.network.Moshi.moshiFeatureAList
import kr.djgis.sspfs.network.Moshi.moshiFeatureBList
import kr.djgis.sspfs.network.Moshi.moshiFeatureCList
import kr.djgis.sspfs.network.Moshi.moshiFeatureDList
import kr.djgis.sspfs.network.Moshi.moshiFeatureEList
import kr.djgis.sspfs.network.Moshi.moshiFeatureFList
import kr.djgis.sspfs.network.Moshi.moshiFeatureList
import kr.djgis.sspfs.network.RetrofitClient

class FeatureViewModel : ViewModel() {

    private val retrofit = RetrofitClient

    private val _feature = MutableLiveData<Feature>()
    private val _featureA = MutableLiveData<FeatureA>()
    private val _featureB = MutableLiveData<FeatureB>()
    private val _featureC = MutableLiveData<FeatureC>()
    private val _featureD = MutableLiveData<FeatureD>()
    private val _featureE = MutableLiveData<FeatureE>()
    private val _featureF = MutableLiveData<FeatureF>()
    val feature: LiveData<Feature> = _feature
    val featureA: LiveData<FeatureA> = _featureA
    val featureB: LiveData<FeatureB> = _featureB
    val featureC: LiveData<FeatureC> = _featureC
    val featureD: LiveData<FeatureD> = _featureD
    val featureE: LiveData<FeatureE> = _featureE
    val featureF: LiveData<FeatureF> = _featureF

    fun value(): Feature {
        return feature.value!!
    }

    fun setCurrentFeature(featureA: FeatureA) {
        _featureA.value = featureA
    }

    fun setCurrentFeature(featureB: FeatureB) {
        _featureB.value = featureB
    }

    fun setCurrentFeature(featureC: FeatureC) {
        _featureC.value = featureC
    }

    fun setCurrentFeature(featureD: FeatureD) {
        _featureD.value = featureD
    }

    fun setCurrentFeature(featureE: FeatureE) {
        _featureE.value = featureE
    }

    fun setCurrentFeature(featureF: FeatureF) {
        _featureF.value = featureF
    }

    fun featuresGet(xmin: Double, ymin: Double, xmax: Double, ymax: Double) = liveData {
        withContext(Dispatchers.IO) {
            val jsonObject = retrofit.featuresGet(xmin, ymin, xmax, ymax)
            val featureList = moshiFeatureList.fromJson(jsonObject.toString())
            emit(featureList!!)
        }
    }

    fun featureGet(fac_typ: String, fac_uid: String) = liveData {
        withContext(Dispatchers.IO) {
            val jsonObject = retrofit.featureGet(fac_uid)
            when (fac_typ) {
                "A" -> {
                    val featureList = moshiFeatureAList.fromJson(jsonObject.toString())
                    emit(featureList!!.features.first())
                }
                "B" -> {
                    val featureList = moshiFeatureBList.fromJson(jsonObject.toString())
                    emit(featureList!!.features.first())
                }
                "C" -> {
                    val featureList = moshiFeatureCList.fromJson(jsonObject.toString())
                    emit(featureList!!.features.first())
                }
                "D" -> {
                    val featureList = moshiFeatureDList.fromJson(jsonObject.toString())
                    emit(featureList!!.features.first())
                }
                "E" -> {
                    val featureList = moshiFeatureEList.fromJson(jsonObject.toString())
                    emit(featureList!!.features.first())
                }
                "F" -> {
                    val featureList = moshiFeatureFList.fromJson(jsonObject.toString())
                    emit(featureList!!.features.first())
                }
                else -> {
                    val featureList = moshiFeatureFList.fromJson(jsonObject.toString())
                    emit(featureList!!.features.first())
                }
            }
        }
    }

/*    suspend fun fromLatLng(feature: Feature): String? {
        val latLng = feature.geom!!.latLngs[0][0] // FIXME: 시점이 아닌 중점으로 검색할 수 있는 방법
        val jsonObject = retrofit.kakaoCoord2Address(latLng.longitude, latLng.latitude)
        val coord2Address = Moshi.moshiCoord2Address.fromJson(jsonObject.toString())
        return coord2Address?.let { (documents, meta) ->
            when (meta.total_count) {
                1 -> documents[0].address.address_name
                else -> "주소 정보 없음"
            }
        }
    }*/
}

object FeatureVMFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST") return FeatureViewModel() as T
    }
}
