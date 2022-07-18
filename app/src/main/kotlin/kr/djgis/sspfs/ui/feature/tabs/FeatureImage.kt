/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature.tabs

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.button.MaterialButton
import kr.djgis.sspfs.R
import kr.djgis.sspfs.data.*
import kr.djgis.sspfs.databinding.FragmentFeatureImageBinding
import kr.djgis.sspfs.ui.feature.attachment.FeatureAttachmentAdapter
import kr.djgis.sspfs.ui.feature.attachment.FeatureAttachmentDecoration
import java.text.SimpleDateFormat
import java.util.*


class FeatureImage(val type: String, val position: String) : FeatureTabs() {

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentFeatureImageBinding? = null
    private val binding get() = _binding!!

    private lateinit var feature: Feature
    private lateinit var featureAttachmentAdapter: FeatureAttachmentAdapter

    private lateinit var photoSharedURI_Q_N_OVER: Uri
    private var reqWidth: Int? = null
    private var reqHeight: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        reqWidth = resources.getDimensionPixelSize(R.dimen.request_image_width)
        reqHeight = resources.getDimensionPixelSize(R.dimen.request_image_height)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeatureImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = super.viewModel
        }

        binding.run {
//            setTableLayoutOnClickListener(fac_typ = type, table = table1)

            featureAttachmentAdapter =
                FeatureAttachmentAdapter(FeatureAttachmentAdapter.OnClickListener { view, attachment ->
                    takePictureFullSize_Shared()
                })
            attachmentRecyclerView.apply {
                layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
                adapter = featureAttachmentAdapter
                addItemDecoration(FeatureAttachmentDecoration())
            }
        }

        viewModel.of(type).observe(viewLifecycleOwner) {
            feature = when (type) {
                "a" -> it as FeatureA
                "b" -> it as FeatureB
                "c" -> it as FeatureC
                "d" -> it as FeatureD
                "e" -> it as FeatureE
                "f" -> it as FeatureF
                else -> it
            }
            featureAttachmentAdapter.submitList(feature.img_fac)

            val buttonTexts = mutableListOf(
                "전경사진",
                "주변현황사진",
                "상류구간 현황사진",
                "하류구간 현황사진",
                "부분파손, 균열 현황사진",
                "철근노출 부식 현황사진",
                "교각, 교대 세굴피해 현황사진",
                "날개벽 현황사진",
            )
            for (t in buttonTexts) {
                val button = MaterialButton(requireContext(), null, R.attr.myButtonStyle).apply {
                    icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_round_add_24, null)
                    text = t
                    setOnClickListener {
                        feature.img_fac!!.add(
                            FeatureAttachment(
                                null, t, "preset"
                            )
                        ).also {
                            featureAttachmentAdapter.submitList(feature.img_fac)
                            featureAttachmentAdapter.notifyDataSetChanged()
                        }
                    }
                }
                binding.attachmentGridView.addView(button)
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun takePictureFullSize_Shared() {
        val fullSizePictureIntent = getPictureIntent_Shared_Q_N_Over(requireContext())
        fullSizePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
            startActivityForResult(
                fullSizePictureIntent, Companion.REQ_IMG_CAPTURE_FULL_SIZE_SHARED_Q_AND_OVER
            )
        }
    }

    /**
     * 공유 영역 저장
     * Android Q 이상일 경우, API 29 ~ (Android 10.0 ~ )
     * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getPictureIntent_Shared_Q_N_Over(context: Context): Intent {
        photoSharedURI_Q_N_OVER = Uri.EMPTY
        val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.KOREAN).format(System.currentTimeMillis())
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "${feature.fac_uid}_${timeStamp}.jpg")
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        //'RELATIVE_PATH', RequiresApi Q
        contentValues.put(
            MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/소규모공공시설/${
                SimpleDateFormat(DIRECTORY_FORMAT, Locale.KOREAN).format(System.currentTimeMillis())
            }"
        )

        //URI 형식 EX) content://media/external/images/media/1006
        photoSharedURI_Q_N_OVER = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ) ?: Uri.EMPTY

        val fullSizeCaptureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        fullSizeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoSharedURI_Q_N_OVER)
        return fullSizeCaptureIntent
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Companion.REQ_IMG_CAPTURE_FULL_SIZE_SHARED_Q_AND_OVER && resultCode == Activity.RESULT_OK) {
//            val options = BitmapFactory.Options()
//            options.inJustDecodeBounds = true
//            try {
//                var inputStream: InputStream? = FileInputStream(filePath)
//                BitmapFactory.decodeStream(inputStream, null, options)
//                inputStream!!.close()
//                inputStream = null
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//            val height = options.outHeight
//            val width = options.outWidth
//            var inSampleSize = 1
//            if (height > reqHeight!! || width > reqWidth!!) {
//                val heightRatio = (height.toFloat() / reqHeight!!.toFloat()).roundToInt().toInt()
//                val widthtRatio = (width.toFloat() / reqWidth!!.toFloat()).roundToInt().toInt()
//                inSampleSize = if (heightRatio < widthtRatio) heightRatio else widthtRatio
//            }
//            val imgOptions = BitmapFactory.Options()
//            imgOptions.inSampleSize = inSampleSize
//            val bitmap = BitmapFactory.decodeFile(filePath.absolutePath, imgOptions)
            val bitmap = data?.extras?.get("data")
//            glide(bitmap).into(binding.attachmentRecyclerView)
        }
    }

    override var text = "현장 사진"

    override var iconDrawable = R.drawable.ic_round_photo_24

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val DIRECTORY_FORMAT = "yyyyMMdd"
        private const val FILENAME_FORMAT = "_yyyyMMdd_HHmmss"
        const val REQ_IMG_CAPTURE_FULL_SIZE_SHARED_Q_AND_OVER = 600//공용공간 Q이상
    }
}
