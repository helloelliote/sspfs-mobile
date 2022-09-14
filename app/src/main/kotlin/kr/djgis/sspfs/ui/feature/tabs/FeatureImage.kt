/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature.tabs

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
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
import kr.djgis.sspfs.ui.feature.Const.IMAGE_PRESET_A
import kr.djgis.sspfs.ui.feature.Const.IMAGE_PRESET_B
import kr.djgis.sspfs.ui.feature.Const.IMAGE_PRESET_C
import kr.djgis.sspfs.ui.feature.Const.IMAGE_PRESET_D
import kr.djgis.sspfs.ui.feature.Const.IMAGE_PRESET_E
import kr.djgis.sspfs.ui.feature.Const.IMAGE_PRESET_F
import kr.djgis.sspfs.ui.feature.attachment.FeatureAttachmentAdapter
import kr.djgis.sspfs.ui.feature.attachment.FeatureAttachmentAdapterListener
import kr.djgis.sspfs.ui.feature.attachment.FeatureAttachmentDecoration
import kr.djgis.sspfs.util.*
import java.text.SimpleDateFormat
import java.util.*


class FeatureImage(val type: String) : FeatureTabs(), FeatureAttachmentAdapterListener {

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentFeatureImageBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentFeature: Feature
    private lateinit var featureAttachmentAdapter: FeatureAttachmentAdapter

    private lateinit var photoSharedURI_Q_N_OVER: Uri
    private var currentAttachment: FeatureAttachment? = null
    private var reqWidth: Int? = null
    private var reqHeight: Int? = null

    private val cardViewMap = mutableMapOf<Int, View>()
    private val attachmentMap = mutableMapOf<Uri, FeatureAttachment>()

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
            featureAttachmentAdapter = FeatureAttachmentAdapter(this@FeatureImage)
            attachmentRecyclerView.apply {
                layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, true)
                adapter = featureAttachmentAdapter
                addItemDecoration(FeatureAttachmentDecoration())
            }
        }

        viewModel.to(type).observeFeatureOnce(viewLifecycleOwner, type) { _feature ->
            currentFeature = _feature
            featureAttachmentAdapter.submitList(currentFeature.img_fac)

            val buttonTextPreset = when (type) {
                "A" -> IMAGE_PRESET_A
                "B" -> IMAGE_PRESET_B
                "C" -> IMAGE_PRESET_C
                "D" -> IMAGE_PRESET_D
                "E" -> IMAGE_PRESET_E
                "F" -> IMAGE_PRESET_F
                else -> listOf("")
            }
            for (txt in buttonTextPreset) {
                val button = MaterialButton(requireContext(), null, R.attr.myButtonStyle).apply {
                    text = txt
                    icon = ResourcesCompat.getDrawable(resources, R.drawable.ic_round_add_24, null)
                    setOnClickListener {
                        currentFeature.img_fac.add(FeatureAttachment(null, txt, "preset")).also {
                            featureAttachmentAdapter.submitList(currentFeature.img_fac)
                            featureAttachmentAdapter.notifyDataSetChanged()
                        }
                    }
                }
                binding.attachmentGridView.addView(button)
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun selectGallery() {
        Intent(Intent.ACTION_PICK).apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        }.also {
            startActivityForResult(it, REQ_IMG_GALLERY_FULL_SIZE_SHARED_Q_AND_OVER)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun takePicture(attachment: FeatureAttachment) {
        val fullSizePictureIntent = getPictureIntent_Shared_Q_N_Over(attachment)
        fullSizePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
            try {
                startActivityForResult(
                    fullSizePictureIntent, REQ_IMG_CAPTURE_FULL_SIZE_SHARED_Q_AND_OVER
                )
            } catch (e: SecurityException) {
                snackbar(
                    requireActivity().findViewById(R.id.fab_main), "카메라 권한이 허용되지 않아 촬영할 수 없습니다."
                ).setAction("확인") {}.show()
            }
        }
    }

    /**
     * 공유 영역 저장
     * Android Q 이상일 경우, API 29 ~ (Android 10.0 ~ )
     * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getPictureIntent_Shared_Q_N_Over(attachment: FeatureAttachment): Intent {
        photoSharedURI_Q_N_OVER = Uri.EMPTY
        val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.KOREAN).format(System.currentTimeMillis())
        val contentValues = ContentValues()
        contentValues.put(
            MediaStore.MediaColumns.DISPLAY_NAME,
            "${currentFeature.fac_uid}_${timeStamp}_${attachment.name?.substringAfterLast("_")}"
        )
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        //'RELATIVE_PATH', RequiresApi Q
        contentValues.put(
            MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/소규모공공시설/${
                SimpleDateFormat(DIRECTORY_FORMAT, Locale.KOREAN).format(System.currentTimeMillis())
            }"
        )

        //URI 형식 EX) content://media/external/images/media/1006
        photoSharedURI_Q_N_OVER = requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
        ) ?: Uri.EMPTY

        val fullSizeCaptureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        fullSizeCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoSharedURI_Q_N_OVER)
        return fullSizeCaptureIntent
    }

    @SuppressLint("NotifyDataSetChanged")
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQ_IMG_CAPTURE_FULL_SIZE_SHARED_Q_AND_OVER -> {
                    attachmentMap[photoSharedURI_Q_N_OVER] = currentAttachment!!
                    currentAttachment = null
                    resolveContentForCapture(photoSharedURI_Q_N_OVER)
                }

                REQ_IMG_GALLERY_FULL_SIZE_SHARED_Q_AND_OVER -> {
                    intent?.data.let {
                        attachmentMap[it!!] = currentAttachment!!
                        currentAttachment = null
                        resolveContentForGallery(it)
                    }
                }
            }
        }
        featureAttachmentAdapter.notifyDataSetChanged()
    }

    private fun resolveContentForCapture(_uri: Uri) {
        requireContext().contentResolver.query(_uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            attachmentMap[_uri]?.apply {
                name = null
                name = cursor.getString(nameIndex)
                uri = _uri
//                url = URL("${BASE_URL}api/images/${cursor.getString(nameIndex)}").toString()
            }
        }
    }

    private fun resolveContentForGallery(_uri: Uri) {
        requireContext().contentResolver.query(_uri, null, null, null, null)?.use { cursor ->
            val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.KOREAN).format(System.currentTimeMillis())
            var newName =
                "${currentFeature.fac_uid}_${timeStamp}_${attachmentMap[_uri]!!.name?.substringAfterLast("_")}"
            if (!newName.contains(".jpg")) newName += ".jpg"
            cursor.moveToFirst()
            attachmentMap[_uri]?.apply {
                name = null
                name = newName
                uri = _uri
//                url = URL("${BASE_URL}api/images/$newName").toString()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onClick(view: View, attachment: FeatureAttachment) {
        when (view.id) {
            R.id.attachment_camera -> {
                currentAttachment = attachment
                cardViewMap[view.hashCode()] = view.rootView
                takePicture(attachment)
            }

            R.id.attachment_gallery -> {
                currentAttachment = attachment
                cardViewMap[view.hashCode()] = view.rootView
                selectGallery()
            }

            R.id.attachment_remove -> {
                alertDialog(
                    title = "사진을 삭제합니까?", message = resources.getString(R.string.feature_image_remove)
                ).setNegativeButton("취소") { dialog, which ->
                }.setPositiveButton("삭제") { dialog, which ->
                    currentFeature.img_fac.remove(attachment).also {
                        featureAttachmentAdapter.submitList(currentFeature.img_fac)
                        featureAttachmentAdapter.notifyDataSetChanged()
                    }
                }.show()
            }
        }
    }

    override fun onLongClick(view: View, attachment: FeatureAttachment): Boolean {
        return true
    }

    override fun onStarChanged(attachment: FeatureAttachment, newValue: Boolean) {
    }

    override fun onArchived(attachment: FeatureAttachment) {
    }

    override var text = "현장 사진"

    override var iconDrawable = R.drawable.ic_round_photo_24

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val DIRECTORY_FORMAT = "yyyyMMdd"
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
        const val REQ_IMG_CAPTURE_FULL_SIZE_SHARED_Q_AND_OVER = 600//공용공간 Q이상
        const val REQ_IMG_GALLERY_FULL_SIZE_SHARED_Q_AND_OVER = 700//공용공간 Q이상
    }
}
