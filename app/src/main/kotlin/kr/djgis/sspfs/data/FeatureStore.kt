/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.data

import androidx.lifecycle.MutableLiveData

object FeatureStore {

    private val allFeatures = mutableSetOf<Feature>()

    val FEATURES: MutableLiveData<Set<Feature>> = MutableLiveData()

    val CURRENT_FEATURE: MutableLiveData<Feature> = MutableLiveData()

    init {
        FEATURES.value = allFeatures
    }
}
