/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.util

import androidx.lifecycle.MutableLiveData

class ListLiveData<T> : MutableLiveData<MutableList<T>?>() {

    init {
        value = mutableListOf()
    }

    val all: MutableList<T>
        get()  {
            return value!!
        }

    val size: Int
        get() {
            return value!!.size
        }

    fun add(item: T) {
        val items: MutableList<T>? = value
        items?.add(item)
        value = items
    }

    fun addAll(list: List<T>?) {
        val items: MutableList<T>? = value
        items?.addAll(list!!)
        value = items
    }

    fun clear(notify: Boolean) {
        val items: MutableList<T>? = value
        items?.clear()
        if (notify) {
            value = items
        }
    }

    fun remove(item: T) {
        val items: MutableList<T>? = value
        items?.remove(item)
        value = items
    }

    fun removeLast() {
        val items: MutableList<T>? = value
        items?.removeLast()
        value = items
    }

    fun notifyChange() {
        val items: MutableList<T>? = value
        value = items
    }
}
