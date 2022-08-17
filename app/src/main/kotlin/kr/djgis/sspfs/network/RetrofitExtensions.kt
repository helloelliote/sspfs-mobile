/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

interface CallbackT<T> : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) onResponse(response = response.body()!!)
        else onFailure(call, Throwable("네트워크 에러: [${response.code()}] ${response.message()}"))
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        onFailure(Throwable("네트워크 에러: [FAIL] ${t.message}").message!!)
    }

    fun onResponse(response: T) = Unit

    fun onFailure(throwable: String) = Unit
}
