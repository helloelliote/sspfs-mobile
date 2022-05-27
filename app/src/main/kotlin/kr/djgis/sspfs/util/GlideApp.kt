/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.util

import android.graphics.Bitmap
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.signature.ObjectKey

@GlideModule
class AppGlideModule: AppGlideModule()

fun GlideRequest<Bitmap>.refresh(): GlideRequest<Bitmap> {
    return this.signature(ObjectKey(System.currentTimeMillis().toString()))
}
