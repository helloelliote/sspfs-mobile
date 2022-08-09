/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kr.djgis.sspfs.data.*

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun <T> LiveData<T>.observeFeatureOnce(lifecycleOwner: LifecycleOwner, fac_typ: String, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            when (fac_typ) {
                "A" -> t as FeatureA
                "B" -> t as FeatureB
                "C" -> t as FeatureC
                "D" -> t as FeatureD
                "E" -> t as FeatureE
                "F" -> t as FeatureF
                else -> t as Feature
            }
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}
