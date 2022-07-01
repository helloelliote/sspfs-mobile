/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.DelicateCoroutinesApi
import kr.djgis.sspfs.databinding.FragmentFeatureAEdit1Binding

@DelicateCoroutinesApi
class FeatureAEdit1 : FeatureTabs() {

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentFeatureAEdit1Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeatureAEdit1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }

        binding.run {
            setTableLayoutOnClickListener(table1)
        }

        viewModel.featureA.observe(viewLifecycleOwner) {
//            if (it.fac_adm.isNullOrEmpty()) coroutineJob = GlobalScope.launch(Dispatchers.Main) {
//                val d1 = withContext(Dispatchers.Default) { viewModel.fromLatLng(it) }
//                if (_binding != null) binding.facAdm.text = d1
//                it.fac_adm = d1
//           }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
