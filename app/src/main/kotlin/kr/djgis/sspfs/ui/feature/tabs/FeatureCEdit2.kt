/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.djgis.sspfs.databinding.FragmentFeatureCEdit2Binding

class FeatureCEdit2 : FeatureTabs() {

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentFeatureCEdit2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeatureCEdit2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = super.viewModel
        }

        binding.run {
            setTableLayoutOnClickListener(fac_typ = "C", table = table1)
        }

        viewModel.featureC.observe(viewLifecycleOwner) {

        }
    }

    override var text = "취입보 조사 2"

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
