/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.model

import android.app.Application
import androidx.annotation.IdRes
import androidx.lifecycle.*
import com.naver.maps.geometry.LatLng
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.djgis.sspfs.App
import kr.djgis.sspfs.R
import kr.djgis.sspfs.data.FeatureList
import kr.djgis.sspfs.network.Moshi.moshiFeatureList
import kr.djgis.sspfs.network.RetrofitClient
import kr.djgis.sspfs.util.ListLiveData
import java.io.Serializable

class FeatureEditViewModel(app: Application) : AndroidViewModel(app) {

    private val _feature: MutableLiveData<Feature> = MutableLiveData(Feature("", listOf()))
    val feature: LiveData<Feature> = _feature

    val latLngs: ListLiveData<LatLng> = ListLiveData()

    val size get() = latLngs.size

    val coords get() = latLngs.all

    private fun featureCreate(fac_typ: String) = liveData {
        withContext(Dispatchers.IO) {
            _feature.value!!.fac_typ = fac_typ
            val jsonObject = RetrofitClient.createFeaturePost(feature.value!!)
            val featureList = moshiFeatureList.fromJson(jsonObject.toString())
            emit(featureList!!)
        }
    }

    fun update() {
        _feature.value?.geom = latLngs.all
    }

    fun add(@IdRes id: Int): LiveData<FeatureList> {
        return when (id) {
            R.id.action_a -> featureCreate("a")
            R.id.action_b -> featureCreate("b")
            R.id.action_c -> featureCreate("c")
            R.id.action_e -> featureCreate("d")
            R.id.action_f -> featureCreate("e")
            else -> featureCreate("")
        }
    }

    @JsonClass(generateAdapter = true)
    data class Feature(
        var fac_typ: String,
        var geom: List<LatLng>,
    ) : Serializable
}

object FeatureEditVMFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST") return FeatureEditViewModel(App()) as T
    }
}
