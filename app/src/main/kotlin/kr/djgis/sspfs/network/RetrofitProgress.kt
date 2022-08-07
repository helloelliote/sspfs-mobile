/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.IOException

class RetrofitProgress(
    private val context: Context,
    private val uri: Uri,
    private val contentType: String,
    private val callback: MultipartUploadCallback,
) : RequestBody() {

    interface MultipartUploadCallback {
        fun onInitiate(percentage: Int)
        fun onProgress(percentage: Int)
        fun onError()
        fun onFinish(percentage: Int)
    }

    override fun contentLength(): Long = -1L

    override fun contentType(): MediaType? = "$contentType/*".toMediaTypeOrNull()

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val file = File(uriToFilePath(uri))
        val totalSize = file.length()
        val buffer = ByteArray(2048)
        context.contentResolver.openInputStream(uri).use { inputStream ->
            var uploadSize = 0L
            var readSize: Int
            var number = 0
            val handler = Handler(Looper.getMainLooper())
            if (inputStream != null) {
                while (inputStream.read(buffer).also { readSize = it } != -1) {
                    val progress = (100 * uploadSize / totalSize).toInt()
                    if (progress > number + 1) {
                        // update progress on UI thread
                        handler.post(ProgressUpdater(uploadSize, totalSize))
                        number = progress
                    }
                    uploadSize += readSize.toLong()
                    sink.write(buffer, 0, readSize)
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun uriToFilePath(uri: Uri?): String {
        val cursor = context.contentResolver.query(uri!!, arrayOf("_data"), null, null, null)
        var path = ""
        cursor?.use {
            it.moveToNext()
            path = it.getString(it.getColumnIndex("_data"))
        }
        return path
    }

    private inner class ProgressUpdater(
        private val uploadSize: Long,
        private val totalSize: Long,
    ) : Runnable {
        override fun run() = callback.onProgress((100 * uploadSize / totalSize).toInt())
    }
}
