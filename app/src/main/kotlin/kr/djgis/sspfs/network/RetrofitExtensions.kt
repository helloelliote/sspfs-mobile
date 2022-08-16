/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.network

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun Call<JsonObject>.enqueue(onResponse: (json: JsonObject) -> Unit, onFailure: (String) -> Unit) {
    this.enqueue(object : Callback<JsonObject> {
        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
            response.let {
                if (it.isSuccessful) {
                    onResponse(it.body()!!)
                } else onFailure(call, Throwable("네트워크 에러: [${it.code()}] ${it.message()}"))
            }
        }

        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
            onFailure(t.message!!)
        }
    })
}
