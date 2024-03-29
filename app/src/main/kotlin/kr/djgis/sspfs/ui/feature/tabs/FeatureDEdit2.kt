/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.djgis.sspfs.databinding.FragmentFeatureDEdit2Binding

class FeatureDEdit2 : FeatureTabs() {

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentFeatureDEdit2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeatureDEdit2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = super.viewModel
        }

        binding.run {
            setTableLayoutOnClickListener(fac_typ = "D", table = table1)
        }

        viewModel.featureD.observe(viewLifecycleOwner) {

        }
    }

    override var text = "낙차공 조사 2"

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
