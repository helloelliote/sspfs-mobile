/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.djgis.sspfs.network.Moshi.moshiKeyword
import kr.djgis.sspfs.network.RetrofitClient

class PlacesViewModel : ViewModel() {

    fun searchPlaces(query: String, x: Double?, y: Double?) = liveData {
        withContext(Dispatchers.IO) {
            val jsonObject = RetrofitClient.kakaoSearchPlaces(query, x, y)
            val keyword = moshiKeyword.fromJson(jsonObject.toString())
            emit(keyword!!)
        }
    }
}

object PlacesVMFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST") return PlacesViewModel() as T
    }
}
