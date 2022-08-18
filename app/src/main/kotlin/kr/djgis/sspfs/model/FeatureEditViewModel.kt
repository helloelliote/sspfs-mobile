/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.model

import androidx.annotation.IdRes
import androidx.lifecycle.*
import com.naver.maps.geometry.LatLng
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kr.djgis.sspfs.R
import kr.djgis.sspfs.data.Result
import kr.djgis.sspfs.network.RetrofitClient.webService
import kr.djgis.sspfs.util.ListLiveData

class FeatureEditViewModel : BaseViewModel() {

    private val _feature: MutableLiveData<Feature> = MutableLiveData(Feature("", "", "", listOf()))
    val feature: LiveData<Feature> = _feature
    val latLngs: ListLiveData<LatLng> = ListLiveData()

    val size get() = latLngs.size

    val coords get() = latLngs.all

    fun createFeature(@IdRes id: Int): LiveData<Result> {
        val liveData = MutableLiveData<Result>()
        viewModelScope.safeLaunch {
            liveData.postValue(
                when (id) {
                    R.id.action_a -> webService.createFeaturePoint(
                        Feature("A", "B", "소교량", coords)
                    )

                    R.id.action_b -> webService.createFeatureLine(
                        Feature("B", "S", "세천", coords)
                    )

                    R.id.action_c -> webService.createFeaturePoint(
                        Feature("C", "W", "취입보", coords)
                    )

                    R.id.action_d -> webService.createFeaturePoint(
                        Feature("D", "W", "낙차공", coords)
                    )

                    R.id.action_e -> webService.createFeatureLine(
                        Feature("E", "N", "농로", coords)
                    )

                    R.id.action_f -> webService.createFeatureLine(
                        Feature("F", "M", "마을진입로", coords)
                    )

                    else -> {
                        webService.createFeatureLine(feature.value!!)
                    }
                }.body()
            )
        }
        return liveData
    }

    fun update() {
        _feature.value?.geom = latLngs.all
    }

    @JsonClass(generateAdapter = true)
    data class FeatureList(
        @field:Json(name = "rows") val features: MutableSet<Feature>,
    )

    @JsonClass(generateAdapter = true)
    data class Feature(
        var fac_typ: String,
        var fac_typ_alt: String,
        var fac_typ_nam: String,
        var geom: List<LatLng>,
    )
}

object FeatureEditVMFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST") return FeatureEditViewModel() as T
    }
}
