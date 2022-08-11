/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.model

import android.app.Application
import androidx.annotation.IdRes
import androidx.lifecycle.*
import com.naver.maps.geometry.LatLng
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kr.djgis.sspfs.App
import kr.djgis.sspfs.R
import kr.djgis.sspfs.network.Moshi.moshiFeatureEditList
import kr.djgis.sspfs.network.RetrofitClient
import kr.djgis.sspfs.util.ListLiveData
import java.io.Serializable

class FeatureEditViewModel(app: Application) : AndroidViewModel(app) {

    private val _feature: MutableLiveData<Feature> = MutableLiveData(Feature("", "", "", listOf()))
    val feature: LiveData<Feature> = _feature

    val latLngs: ListLiveData<LatLng> = ListLiveData()

    val size get() = latLngs.size

    val coords get() = latLngs.all

    private fun createFeaturePoint(fac_typ: String, fac_typ_alt: String, fac_typ_nam: String) = liveData {
        _feature.value?.apply {
            this.fac_typ = fac_typ
            this.fac_typ_alt = fac_typ_alt
            this.fac_typ_nam = fac_typ_nam
        }
        val jsonObject = RetrofitClient.createFeaturePoint(feature.value!!)
        val featureList = moshiFeatureEditList.fromJson(jsonObject.toString())
        emit(featureList!!)
    }

    private fun createFeatureLine(fac_typ: String, fac_typ_alt: String, fac_typ_nam: String) = liveData {
        _feature.value?.apply {
            this.fac_typ = fac_typ
            this.fac_typ_alt = fac_typ_alt
            this.fac_typ_nam = fac_typ_nam
        }
        val jsonObject = RetrofitClient.createFeatureLine(feature.value!!)
        val featureList = moshiFeatureEditList.fromJson(jsonObject.toString())
        emit(featureList!!)
    }

    fun update() {
        _feature.value?.geom = latLngs.all
    }

    fun add(@IdRes id: Int): LiveData<FeatureList> {
        return when (id) {
            R.id.action_a -> createFeaturePoint("A", "B", "소교량")
            R.id.action_b -> createFeatureLine("B", "S", "세천")
            R.id.action_c -> createFeaturePoint("C", "W", "취입보")
//            R.id.action_d -> featureCreate("D", "W", "낙차공")
            R.id.action_e -> createFeatureLine("E", "N", "농로")
            R.id.action_f -> createFeatureLine("F", "M", "마을진입로")
            else -> createFeatureLine("", "", "")
        }
    }

    data class FeatureList(
        @Json(name = "rows") val features: MutableSet<Feature>,
    )

    @JsonClass(generateAdapter = true)
    data class Feature(
        var fac_typ: String,
        var fac_typ_alt: String,
        var fac_typ_nam: String,
        var geom: List<LatLng>,
    ) : Serializable
}

object FeatureEditVMFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST") return FeatureEditViewModel(App()) as T
    }
}
