/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.djgis.sspfs.network.Moshi.moshiCoord2Regioncode
import kr.djgis.sspfs.network.RetrofitClient

class Coord2RegioncodeViewModel : ViewModel() {

    private val retrofit = RetrofitClient

    fun coord2Regioncode(x: Double, y: Double, input: String? = null, output: String? = null) = liveData {
        withContext(Dispatchers.IO) {
            val jsonObject = retrofit.kakaoCoord2Regioncode(x, y, input, output)
            val coord2Regioncode = moshiCoord2Regioncode.fromJson(jsonObject.toString())
            emit(coord2Regioncode!!)
        }
    }
}

object Coord2RegioncodeVMFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST") return Coord2RegioncodeViewModel() as T
    }
}
