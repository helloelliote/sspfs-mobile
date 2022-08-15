/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.model

import android.app.Application
import androidx.annotation.IdRes
import androidx.lifecycle.*
import com.google.gson.JsonObject
import com.naver.maps.geometry.LatLng
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kr.djgis.sspfs.App
import kr.djgis.sspfs.R
import kr.djgis.sspfs.network.RetrofitClient.webService
import kr.djgis.sspfs.util.ListLiveData
import retrofit2.Call
import java.io.Serializable

class FeatureEditViewModel(app: Application) : AndroidViewModel(app) {

    private val feature: MutableLiveData<Feature> = MutableLiveData(Feature("", "", "", listOf()))

    val latLngs: ListLiveData<LatLng> = ListLiveData()

    val size get() = latLngs.size

    val coords get() = latLngs.all

    fun createFeature(@IdRes id: Int): Call<JsonObject> {
        when (id) {
            R.id.action_a -> return webService.createFeaturePoint(
                Feature("A", "B", "소교량", coords)
            )

            R.id.action_b -> return webService.createFeatureLine(
                Feature("B", "S", "세천", coords)
            )

            R.id.action_c -> return webService.createFeaturePoint(
                Feature("C", "W", "취입보", coords)
            )

            R.id.action_d -> return webService.createFeaturePoint(
                Feature("D", "W", "낙차공", coords)
            )

            R.id.action_e -> return webService.createFeatureLine(
                Feature("E", "N", "농로", coords)
            )

            R.id.action_f -> return webService.createFeatureLine(
                Feature("F", "M", "마을진입로", coords)
            )

            else -> {
                listOf("", "", "")
                return webService.createFeatureLine(feature.value!!)
            }
        }
    }

    fun update() {
        feature.value?.geom = latLngs.all
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
