/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.djgis.sspfs.data.FeaturePK4
import kr.djgis.sspfs.databinding.FragmentNavDrawerBinding

class NavDrawerFragment : BottomSheetDialogFragment() {

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentNavDrawerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentNavDrawerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            setOf(iconPK4A, iconPK4B, iconPK4C, iconPK4D, iconPK4E, iconPK4F).forEach {
                // @See: https://stackoverflow.com/a/61893468/10521336
                it.compoundDrawables[2]?.setTint(FeaturePK4.toColor(it.contentDescription.toString()))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
