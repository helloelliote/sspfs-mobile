/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.djgis.sspfs.network.Moshi.moshiCoord2Address
import kr.djgis.sspfs.network.RetrofitClient

class Coord2AddressViewModel : ViewModel() {

    private val retrofit = RetrofitClient

    fun coord2Address(x: Double, y: Double, input: String? = null) = liveData {
        withContext(Dispatchers.IO) {
            val jsonObject = retrofit.kakaoCoord2Address(x, y, input)
            val coord2Address = moshiCoord2Address.fromJson(jsonObject.toString())
            emit(coord2Address!!)
        }
    }
}

object Coord2AddressVMFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST") return Coord2AddressViewModel() as T
    }
}
