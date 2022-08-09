/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.model

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.djgis.sspfs.App
import kr.djgis.sspfs.data.*
import kr.djgis.sspfs.network.Moshi.moshiFeatureAList
import kr.djgis.sspfs.network.Moshi.moshiFeatureBList
import kr.djgis.sspfs.network.Moshi.moshiFeatureCList
import kr.djgis.sspfs.network.Moshi.moshiFeatureDList
import kr.djgis.sspfs.network.Moshi.moshiFeatureEList
import kr.djgis.sspfs.network.Moshi.moshiFeatureFList
import kr.djgis.sspfs.network.Moshi.moshiFeatureList
import kr.djgis.sspfs.network.Moshi.moshiRegionList
import kr.djgis.sspfs.network.RetrofitClient
import kr.djgis.sspfs.network.RetrofitProgress
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData

class FeatureViewModel(app: Application) : AndroidViewModel(app) {

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

    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap> = _bitmap

    fun getBitmap() = bitmapStore

    fun setBitmap(bitmap: Bitmap) = bitmapStore.postValue(bitmap)

    fun of(fac_typ: String): LiveData<out Feature> {
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

    fun bitmap(bitmap: Bitmap) {
        _bitmap.value = bitmap
    }

    fun featuresGet(xmin: Double, ymin: Double, xmax: Double, ymax: Double) = liveData {
        withContext(Dispatchers.IO) {
            val jsonObject = RetrofitClient.featuresGet(xmin, ymin, xmax, ymax)
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

    fun featurePost(fac_typ: String, exm_chk: String, callback: RetrofitProgress.MultipartUploadCallback) = liveData {
        withContext(Dispatchers.IO) {
            val multipartBody = mutableListOf<MultipartBody.Part>()
            val feature = this@FeatureViewModel.of(fac_typ).value!!
            feature.exm_chk = exm_chk
            feature.img_fac.forEach { attachment ->
                if (attachment.uri == null) {
                    return@forEach
                } else {
                    val part = createFormData(
                        "files", attachment.name, RetrofitProgress(attachment.uri!!, "image", callback)
                    )
                    multipartBody.add(part)
                }
            }
            val jsonBody = createFormData("json", Gson().toJson(feature))
            val jsonElement = RetrofitClient.featurePost(jsonBody, edit, multipartBody)
            emit(jsonElement)
        }
    }

    fun regionsGet(xmin: Double, ymin: Double, xmax: Double, ymax: Double) = liveData {
        withContext(Dispatchers.IO) {
            val jsonObject = RetrofitClient.regionsGet(xmin, ymin, xmax, ymax)
            val regionList = moshiRegionList.fromJson(jsonObject.toString())
            emit(regionList!!)
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
        @Suppress("UNCHECKED_CAST") return FeatureViewModel(App()) as T
    }
}
