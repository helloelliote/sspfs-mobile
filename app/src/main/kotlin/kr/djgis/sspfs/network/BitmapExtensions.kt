/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.exifinterface.media.ExifInterface
import androidx.exifinterface.media.ExifInterface.*
import kr.djgis.sspfs.App
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun File.resizeImageToRes(maxResolution: Int = 1600): File {
    val newFile = File.createTempFile("TEMP_", null, App.context.cacheDir)
    var bitmap = BitmapFactory.decodeFile(this.path)
    val width = bitmap.width.toFloat()
    val height = bitmap.height.toFloat()
    val options = BitmapFactory.Options()
    options.inSampleSize = 1
    bitmap = BitmapFactory.decodeFile(this.path, options)
    return FileOutputStream(newFile).use { outputStream ->
        if (width < maxResolution.toFloat() && height < maxResolution.toFloat()) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            return@use newFile
        } else {
            val bitmapRatio = width / height
            var newWidth = maxResolution
            var newHeight = maxResolution
            if (1.0f > bitmapRatio) {
                newWidth = (maxResolution.toFloat() * bitmapRatio).toInt()
            } else {
                newHeight = (maxResolution.toFloat() / bitmapRatio).toInt()
            }
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            return@use newFile
        }
    }
}

fun File.preserveExif(oldFile: File): File {
    copyExif(oldFile.path, this.absolutePath).also {
        return this
    }
}

@Throws(IOException::class)
fun copyExif(oldPath: String, newPath: String) {
    val oldExif = ExifInterface(oldPath)
    val attributes = arrayOf(
        TAG_DATETIME,
        TAG_DATETIME_DIGITIZED,
        TAG_EXPOSURE_TIME,
        TAG_FLASH,
        TAG_FOCAL_LENGTH,
        TAG_GPS_ALTITUDE,
        TAG_GPS_ALTITUDE_REF,
        TAG_GPS_DATESTAMP,
        TAG_GPS_LATITUDE,
        TAG_GPS_LATITUDE_REF,
        TAG_GPS_LONGITUDE,
        TAG_GPS_LONGITUDE_REF,
        TAG_GPS_PROCESSING_METHOD,
        TAG_GPS_TIMESTAMP,
        TAG_IMAGE_LENGTH,
        TAG_IMAGE_WIDTH,
        TAG_MAKE,
        TAG_MODEL,
        TAG_ORIENTATION,
        TAG_SUBSEC_TIME,
        TAG_WHITE_BALANCE
    )
    val newExif = ExifInterface(newPath)
    for (i in attributes.indices) {
        val value = oldExif.getAttribute(attributes[i])
        if (value != null) newExif.setAttribute(attributes[i], value)
    }
    newExif.saveAttributes()
}
