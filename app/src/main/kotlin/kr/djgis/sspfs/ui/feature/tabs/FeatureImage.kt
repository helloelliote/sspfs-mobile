/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature.tabs

import android.Manifest.permission.CAMERA
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import kr.djgis.sspfs.Config.BASE_URL
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
import kr.djgis.sspfs.util.alertDialog
import kr.djgis.sspfs.util.glide
import kr.djgis.sspfs.util.snackbar
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class FeatureImage(val type: String, val position: String) : FeatureTabs(), FeatureAttachmentAdapterListener {

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentFeatureImageBinding? = null
    private val binding get() = _binding!!

    private lateinit var feature: Feature
    private lateinit var featureAttachmentAdapter: FeatureAttachmentAdapter

    private lateinit var currentAttachment: FeatureAttachment
    private lateinit var currentView: View
    private lateinit var photoSharedURI_Q_N_OVER: Uri
    private var reqWidth: Int? = null
    private var reqHeight: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        reqWidth = resources.getDimensionPixelSize(R.dimen.request_image_width)
        reqHeight = resources.getDimensionPixelSize(R.dimen.request_image_height)

        val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (!it) {
                snackbar(
                    requireActivity().findViewById(R.id.fab_main),
                    "카메라 권한이 허용되지 않았습니다. 앱 설정에서 직접 허용해주세요."
                ).setAction("확인") {
                }.show()
            }
        }

        requestPermissionLauncher.launch(CAMERA)
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
            featureAttachmentAdapter =
                FeatureAttachmentAdapter(this@FeatureImage)
            attachmentRecyclerView.apply {
                layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
                adapter = featureAttachmentAdapter
                addItemDecoration(FeatureAttachmentDecoration())
            }
        }

        viewModel.of(type).observe(viewLifecycleOwner) {
            feature = when (type) {
                "A" -> it as FeatureA
                "B" -> it as FeatureB
                "C" -> it as FeatureC
                "D" -> it as FeatureD
                "E" -> it as FeatureE
                "F" -> it as FeatureF
                else -> it
            }
            featureAttachmentAdapter.submitList(feature.img_fac)

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
                        feature.img_fac!!.add(FeatureAttachment(null, txt, "preset")).also {
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
    private fun takePictureFullSize_Shared(attachment: FeatureAttachment) {
        val fullSizePictureIntent = getPictureIntent_Shared_Q_N_Over(requireContext(), attachment)
        fullSizePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
            try {
                startActivityForResult(
                    fullSizePictureIntent, REQ_IMG_CAPTURE_FULL_SIZE_SHARED_Q_AND_OVER
                )
            } catch (e: SecurityException) {
                snackbar(
                    requireActivity().findViewById(R.id.fab_main),
                    "카메라 권한이 허용되지 않아 촬영할 수 없습니다."
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
    fun getPictureIntent_Shared_Q_N_Over(context: Context, attachment: FeatureAttachment): Intent {
        photoSharedURI_Q_N_OVER = Uri.EMPTY
        val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.KOREAN).format(System.currentTimeMillis())
        val contentValues = ContentValues()
        contentValues.put(
            MediaStore.MediaColumns.DISPLAY_NAME,
            "${feature.fac_uid}_${timeStamp}_${attachment.name?.substringAfterLast("_")}.jpg"
        )
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
        if (requestCode == REQ_IMG_CAPTURE_FULL_SIZE_SHARED_Q_AND_OVER && resultCode == Activity.RESULT_OK) {
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
//            val bitmap = data?.extras?.get("data")
            requireContext().contentResolver.query(photoSharedURI_Q_N_OVER, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                currentAttachment.name = null
                currentAttachment.name = cursor.getString(nameIndex)
                currentAttachment.uri = photoSharedURI_Q_N_OVER
                currentAttachment.url = URL("${BASE_URL}api/images/${currentAttachment.name}").toString()
            }
            with(currentView) {
                glide(photoSharedURI_Q_N_OVER, true).into(this.findViewById(R.id.attachment_image) as ImageView)
                (this.findViewById(R.id.attachment_name) as TextInputEditText).setText(currentAttachment.name)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onClick(view: View, attachment: FeatureAttachment) {
        if (attachment.url != null) return
        view as MaterialCardView
        currentView = view
        currentAttachment = attachment
        takePictureFullSize_Shared(currentAttachment)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onLongClick(view: View, attachment: FeatureAttachment): Boolean {
        alertDialog(
            title = "사진을 삭제합니까?",
            message = resources.getString(R.string.feature_image_remove)
        ).setNegativeButton("취소") { dialog, which ->
        }.setPositiveButton("삭제") { dialog, which ->
            feature.img_fac!!.remove(attachment).also {
                featureAttachmentAdapter.submitList(feature.img_fac)
                featureAttachmentAdapter.notifyDataSetChanged()
            }
        }.show()
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
    }
}
