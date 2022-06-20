/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature

import android.app.Activity
import android.os.Bundle
import android.speech.RecognizerIntent.EXTRA_RESULTS
import android.telephony.PhoneNumberFormattingTextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.*
import kr.djgis.sspfs.Config.DATETIME_FORMAT
import kr.djgis.sspfs.Config.DATETIME_ZONE
import kr.djgis.sspfs.R
import kr.djgis.sspfs.data.FeatureAttachment
import kr.djgis.sspfs.data.FeaturePK4
import kr.djgis.sspfs.databinding.FragmentFeatureEditBinding
import kr.djgis.sspfs.model.FeatureVMFactory
import kr.djgis.sspfs.model.FeatureViewModel
import kr.djgis.sspfs.ui.feature.attachment.FeatureAttachmentAdapter
import kr.djgis.sspfs.ui.feature.attachment.FeatureAttachmentAdapterListener
import kr.djgis.sspfs.ui.feature.attachment.FeatureAttachmentDecoration
import kr.djgis.sspfs.util.DecimalFilter
import kr.djgis.sspfs.util.alertDialog
import kr.djgis.sspfs.util.getRecognizeSpeechIntent
import java.time.LocalDateTime.now

@DelicateCoroutinesApi
class FeatureFragmentEdit : Fragment(), FeatureAttachmentAdapterListener {

    private val viewModel: FeatureViewModel by activityViewModels { FeatureVMFactory }

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentFeatureEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var featureAttachmentAdapter: FeatureAttachmentAdapter
    private lateinit var materialDatePicker: MaterialDatePicker<Long>

    private var coroutineJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeatureEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@FeatureFragmentEdit.viewModel
        }

        binding.run {
            val adapterFeaturePk4 = ArrayAdapter(requireContext(), R.layout.feature_pk4_item, FeaturePK4.types())
            (featurePk4Dropdown.editText as? AutoCompleteTextView)?.setAdapter(adapterFeaturePk4)

            featureA1Layout.setEndIconOnClickListener {
                alertDialog(R.string.dialog_title_a1, R.string.dialog_supporting_text_a1).show()
            }

//            featureF1Layout.setEndIconOnClickListener {
//                alertDialog(R.string.dialog_title_f1, R.string.dialog_supporting_text_f1).show()
//            }

            val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val results = it.data?.getStringArrayListExtra(EXTRA_RESULTS)?.get(0)
                    with(featureG1) {
                        when {
                            results == "삭제" -> this.text = null
                            this.text.toString() == "" -> this.setText(results)
                            else -> this.append(" $results")
                        }
                    }
                }
            }

            featureG1Layout.setEndIconOnClickListener {
//                alertDialog(R.string.dialog_title_f2, R.string.dialog_supporting_text_f2).show()
                activityResultLauncher.launch(getRecognizeSpeechIntent("그 밖의 사항(조사자의견)을 말씀하세요\n( \"삭제\"라고 말씀하시면 모든 내용을 초기화합니다 )"))
            }

            featureA4.addTextChangedListener(PhoneNumberFormattingTextWatcher())

            setOf(
                featureA2,
                featureE1,
                featureE2,
                featureE3,
                featureG1,
            ).forEach {
                it.setOnFocusChangeListener { _, b -> if (b) it.setSelection(it.text!!.length) }
            }

            setOf(
                featureE1, featureE2, featureC2,
            ).forEach {
                it.filters = arrayOf(DecimalFilter(4, 2))
                // TODO: it.text.toString().toDouble().toString() 를 사용해 '.05' 나 '3.' 등의 잘못된
            }

            featureAttachmentAdapter = FeatureAttachmentAdapter(this@FeatureFragmentEdit)
            attachmentRecyclerView.apply {
                adapter = featureAttachmentAdapter
                addItemDecoration(FeatureAttachmentDecoration())
            }

            val animationNext = AnimationUtils.loadAnimation(context, R.anim.shake_next)
            val animationPrev = AnimationUtils.loadAnimation(context, R.anim.shake_prev)
            imageButtonNext.startAnimation(animationNext)
            imageButtonPrev.startAnimation(animationPrev)
        }

        viewModel.feature.observe(viewLifecycleOwner) {
            featureAttachmentAdapter.submitList(it.image)

            if (it.d1.isNullOrEmpty()) coroutineJob = GlobalScope.launch(Dispatchers.Main) {
                val d1 = withContext(Dispatchers.Default) { viewModel.fromLatLng(it) }
                if (_binding != null) binding.featureD1.setText(d1)
            }

            if (it.a6.isNullOrEmpty()) {
                binding.featureA6.setText(now(DATETIME_ZONE).format(DATETIME_FORMAT))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineJob?.cancel()
        _binding = null
    }

    override fun onClicked(view: View, attachment: FeatureAttachment) {
        when (view.id) {
            R.id.attachment_camera -> {
//                val directions = FeatureFragmentDirections.actionToCameraFragment()
                val directions = FeatureFragmentDirections.actionToCameraXFragment()
                findNavController().navigate(directions)
            }
        }
    }

    override fun onLongPressed(attachment: FeatureAttachment): Boolean {
        return false
    }

    override fun onStarChanged(attachment: FeatureAttachment, newValue: Boolean) {
    }

    override fun onArchived(attachment: FeatureAttachment) {
    }
}
