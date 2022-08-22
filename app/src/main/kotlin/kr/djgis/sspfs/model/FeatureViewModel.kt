/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.model

import androidx.lifecycle.*
import com.google.gson.Gson
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.overlay.Overlay
import kr.djgis.sspfs.Config
import kr.djgis.sspfs.data.*
import kr.djgis.sspfs.network.RetrofitClient.webService
import kr.djgis.sspfs.network.RetrofitProgress
import kr.djgis.sspfs.network.RetrofitProgress.MultipartUploadCallback
import kr.djgis.sspfs.util.round
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData
import java.net.URL

class FeatureViewModel : BaseViewModel() {

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

    private val _latLngBounds = MutableLiveData<LatLngBounds>()
    val latLngBounds: LiveData<LatLngBounds> = _latLngBounds

    private val _overlay = MutableLiveData<Overlay>()
    val overlay: LiveData<Overlay> = _overlay

    private val _type = MutableLiveData<String>()
    val type: LiveData<String> = _type

    fun latLngBounds(latLngBounds: LatLngBounds): FeatureViewModel {
        _latLngBounds.value = latLngBounds
        return this
    }

    fun overlay(overlay: Overlay): FeatureViewModel {
        _overlay.value = overlay
        return this
    }

    fun to(fac_typ: String): LiveData<out Feature> {
        return when (fac_typ) {
            "A" -> featureA
            "B" -> featureB
            "C" -> featureC
            "D" -> featureD
            "E" -> featureE
            "F" -> featureF
            else -> feature
        }
    }

    fun of(fac_typ: String): Feature {
        return to(fac_typ).value!!
    }

    fun type(fac_typ: String): FeatureViewModel {
        _type.value = fac_typ
        return this
    }

    fun set(feature: Any): FeatureViewModel {
        when (type.value) {
            "A" -> _set(feature as FeatureA)
            "B" -> _set(feature as FeatureB)
            "C" -> _set(feature as FeatureC)
            "D" -> _set(feature as FeatureD)
            "E" -> _set(feature as FeatureE)
            "F" -> _set(feature as FeatureF)
        }
        return this
    }

    private fun _set(featureA: FeatureA) {
        _featureA.value = featureA
    }

    private fun _set(featureB: FeatureB) {
        _featureB.value = featureB
    }

    private fun _set(featureC: FeatureC) {
        _featureC.value = featureC
    }

    private fun _set(featureD: FeatureD) {
        _featureD.value = featureD
    }

    private fun _set(featureE: FeatureE) {
        _featureE.value = featureE
    }

    private fun _set(featureF: FeatureF) {
        _featureF.value = featureF
    }

    fun featuresGet(latLngBounds: LatLngBounds): LiveData<FeatureList> {
        val liveData = MutableLiveData<FeatureList>()
        viewModelScope.safeLaunch {
            val doubles = latLngBounds.round()
            val response = webService.featuresGet(doubles[0], doubles[1], doubles[2], doubles[3])
            if (response.isSuccessful) liveData.postValue(response.body()) else throw Throwable("[${response.code()}] ${response.message()}")
        }
        return liveData
    }

    fun districtGet(latLngBounds: LatLngBounds): LiveData<DistrictList> {
        val liveData = MutableLiveData<DistrictList>()
        viewModelScope.safeLaunch {
            val doubles = latLngBounds.round()
            val response = webService.districtGet(doubles[0], doubles[1], doubles[2], doubles[3])
            if (response.isSuccessful) liveData.postValue(response.body()) else throw Throwable("[${response.code()}] ${response.message()}")
        }
        return liveData
    }

    fun themeGet(latLngBounds: LatLngBounds, name: String): LiveData<ThemeList> {
        val liveData = MutableLiveData<ThemeList>()
        viewModelScope.safeLaunch {
            val doubles = latLngBounds.round()
            val response = webService.themeGet(doubles[0], doubles[1], doubles[2], doubles[3], name)
            if (response.isSuccessful) liveData.postValue(response.body()) else throw Throwable("[${response.code()}] ${response.message()}")
        }
        return liveData
    }

    fun mobileUpdate(version: String): LiveData<Version> {
        val liveData = MutableLiveData<Version>()
        viewModelScope.safeLaunch {
            val response = webService.mobileUpdate(version)
            if (response.isSuccessful) liveData.postValue(response.body()) else throw Throwable("[${response.code()}] ${response.message()}")
        }
        return liveData
    }

    fun featurePost(
        status: String,
        edit: String?,
        fraction: Double?,
        callback: MultipartUploadCallback,
    ): LiveData<Result> {
        val liveData = MutableLiveData<Result>()
        viewModelScope.safeLaunch {
            val multipartBody = mutableListOf<MultipartBody.Part>()
            val feature = this@FeatureViewModel.of(type.value!!)
            feature.exm_chk = status
            feature.img_fac.forEach { attachment ->
                if (attachment.uri == null) {
                    return@forEach
                } else {
                    attachment.url = URL("${Config.BASE_URL}api/images/${attachment.name}").toString()
                    val part = createFormData(
                        "files", attachment.name, RetrofitProgress(attachment.uri!!, "image", callback)
                    )
                    multipartBody.add(part)
                    attachment.uri = null
                }
            }
            val jsonBody = createFormData("json", Gson().toJson(feature))
            val response = webService.featurePost(jsonBody, edit, fraction, multipartBody)
            if (response.isSuccessful) liveData.postValue(response.body()) else throw Throwable("[${response.code()}] ${response.message()}")
        }
        return liveData
    }

    fun featureSwitch(fac_typ: String, fac_uid: String): LiveData<Result> {
        val liveData = MutableLiveData<Result>()
        viewModelScope.safeLaunch {
            val response = webService.featureSwitch(fac_typ, fac_uid)
            if (response.isSuccessful) liveData.postValue(response.body()) else throw Throwable("[${response.code()}] ${response.message()}")
        }
        return liveData
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
