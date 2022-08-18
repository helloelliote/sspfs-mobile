/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.model

import androidx.lifecycle.*
import kr.djgis.sspfs.data.kakao.search.Keyword
import kr.djgis.sspfs.network.RetrofitClient.kakaoService

class KakaoViewModel : BaseViewModel() {

    fun searchKeyword(query: String, x: Double?, y: Double?): LiveData<Keyword> {
        val liveData = MutableLiveData<Keyword>()
        viewModelScope.safeLaunch {
            val response = kakaoService.searchKeyword(query, x, y)
            if (response.isSuccessful) liveData.postValue(response.body()) else throw Throwable("[${response.code()}] ${response.message()}")
        }
        return liveData
    }
}

object KakaoVMFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST") return KakaoViewModel() as T
    }
}
