/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.JsonElement
import com.naver.maps.map.*
import com.naver.maps.map.NaverMap.MapType.Satellite
import kotlinx.coroutines.DelicateCoroutinesApi
import kr.djgis.sspfs.Config.EDIT_GEOM_REVERSE
import kr.djgis.sspfs.Config.EDIT_GEOM_REVERSE_AND_SAVE
import kr.djgis.sspfs.Config.EXM_CHK_EXCLUDE
import kr.djgis.sspfs.Config.EXM_CHK_SAVE
import kr.djgis.sspfs.R
import kr.djgis.sspfs.databinding.FragmentFeatureBinding
import kr.djgis.sspfs.model.FeatureVMFactory
import kr.djgis.sspfs.model.FeatureViewModel
import kr.djgis.sspfs.network.RetrofitProgress.MultipartUploadCallback
import kr.djgis.sspfs.ui.MainActivity
import kr.djgis.sspfs.ui.feature.tabs.*
import kr.djgis.sspfs.util.alertDialog
import kr.djgis.sspfs.util.observeOnce
import kr.djgis.sspfs.util.toggleFab
import java.util.*

@DelicateCoroutinesApi
class FeatureFragment : Fragment(), View.OnClickListener, MultipartUploadCallback, MenuProvider, OnMapReadyCallback {

    private val viewModel: FeatureViewModel by activityViewModels { FeatureVMFactory }

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentFeatureBinding? = null
    private val binding get() = _binding!!

    private val args: FeatureFragmentArgs by navArgs()

    private var adapter: FragmentPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                alertDialog(
                    title = viewModel.of(args.type).fac_nam,
                    message = resources.getString(R.string.feature_back)
                ).setNegativeButton("취소") { _, _ ->
                }.setPositiveButton("나가기") { _, _ ->
                    isEnabled = false
                    activity?.onBackPressed()
                }.show()
            }
        })

        adapter = when (args.type) {
            "A" -> FragmentPagerAdapter(
                this@FeatureFragment, listOf(
                    FeatureAEdit1(),
                    FeatureAEdit2(),
                    FeatureImage(args.type),
                )
            )

            "B" -> {
                FragmentPagerAdapter(
                    this@FeatureFragment, listOf(
                        FeatureBEdit1U(),
                        FeatureBEdit2U(),
                        FeatureBEdit1M(),
                        FeatureBEdit2M(),
                        FeatureBEdit1L(),
                        FeatureBEdit2L(),
                        FeatureImage(args.type),
                    )
                )
            }

            "C" -> FragmentPagerAdapter(
                this@FeatureFragment, listOf(
                    FeatureCEdit1(),
                    FeatureCEdit2(),
                    FeatureImage(args.type),
                )
            )

            "D" -> FragmentPagerAdapter(
                this@FeatureFragment, listOf(
                    FeatureDEdit1(),
                    FeatureDEdit2(),
                    FeatureImage(args.type),
                )
            )

            "E" -> {
                FragmentPagerAdapter(
                    this@FeatureFragment, listOf(
                        FeatureEEdit1U(),
                        FeatureEEdit2U(),
                        FeatureEEdit1M(),
                        FeatureEEdit2M(),
                        FeatureEEdit1L(),
                        FeatureEEdit2L(),
                        FeatureImage(args.type),
                    )
                )
            }

            "F" -> {
                FragmentPagerAdapter(
                    this@FeatureFragment, listOf(
                        FeatureFEdit1U(),
                        FeatureFEdit2U(),
                        FeatureFEdit1M(),
                        FeatureFEdit2M(),
                        FeatureFEdit1L(),
                        FeatureFEdit2L(),
                        FeatureImage(args.type),
                    )
                )
            }

            else -> FragmentPagerAdapter(this@FeatureFragment, listOf())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeatureBinding.inflate(inflater, container, false)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
        }

        binding.run {
            (requireActivity() as MainActivity).setSupportActionBar(toolbar)

            val mapFragment = childFragmentManager.findFragmentById(R.id.toolbar) as MapFragment?
                ?: MapFragment.newInstance(
                    NaverMapOptions()
                        .locale(Locale.KOREA)
                        .mapType(Satellite)
                        .camera(CameraPosition(viewModel.latLngBounds.value!!.center, 18.0))
                        .minZoom(16.0)
                        .extent(viewModel.latLngBounds.value)
                        .tiltGesturesEnabled(false)
                        .zoomGesturesEnabled(true)
                ).also {
                    childFragmentManager.beginTransaction().add(R.id.toolbar, it).commit()
                }
            mapFragment.getMapAsync(this@FeatureFragment)

            viewPager.isUserInputEnabled = false
            viewPager.adapter = adapter
            viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
//            viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
//                override fun onPageSelected(position: Int) {
//                    super.onPageSelected(position)
//                }
//            })
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.apply {
                    val tabItem = (viewPager.adapter as FragmentPagerAdapter).tabs[position] as FeatureTabsInterface
                    text = tabItem.text
                    icon = ResourcesCompat.getDrawable(resources, tabItem.iconDrawable, null)!!
                }
            }.attach()
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        viewModel.overlay.value?.let {
            it.map = naverMap
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_remove -> {
                alertDialog(
                    title = viewModel.of(args.type).fac_nam,
                    message = resources.getString(R.string.feature_action_exclude)
                ).setNegativeButton("취소") { _, _ ->
                }.setPositiveButton("제외") { _, _ ->
                    onSave(EXM_CHK_EXCLUDE) {
                        val directions = FeatureFragmentDirections.actionToNaverMapFragment()
                        findNavController().navigate(directions)
                    }
                }.show()
                return true
            }

            R.id.action_reverse_geom -> {
                alertDialog(
                    title = viewModel.of(args.type).fac_nam,
                    message = resources.getString(R.string.feature_action_geom_reverse)
                ).setNeutralButton("취소") { dialog, _ ->
                    dialog.cancel()
                }.setNegativeButton("반전") { _, _ ->
                    onSave(EXM_CHK_SAVE, EDIT_GEOM_REVERSE) {
                        val directions = FeatureFragmentDirections.actionToNaverMapFragment()
                        findNavController().navigate(directions)
                    }
                }.setPositiveButton("반전 후 저장") { _, _ ->
                    onSave(EXM_CHK_SAVE, EDIT_GEOM_REVERSE_AND_SAVE) {
                        val directions = FeatureFragmentDirections.actionToNaverMapFragment()
                        findNavController().navigate(directions)
                    }
                }.show()
                return true
            }

            else -> false
        }
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.fab_main -> {
                onSave(EXM_CHK_SAVE) {
                    val directions = FeatureFragmentDirections.actionToNaverMapFragment()
                    findNavController().navigate(directions)
                }
            }
        }
    }

    private fun onSave(exm_chk: String, edit: String? = null, observer: Observer<JsonElement>) {
        binding.viewPagerCover.visibility = View.VISIBLE
        viewModel.type(args.type).featurePost(exm_chk, edit, this@FeatureFragment)
            .observeOnce(viewLifecycleOwner, observer)
    }

    override fun onInitiate(percentage: Int) {
        toggleFab(false, R.color.light_green_A200, R.drawable.ic_round_save_30)
        binding.progressCircular.setProgressCompat(percentage, true)
    }

    override fun onProgress(percentage: Int) {
        binding.progressText.text = getString(R.string.progress_percentage, percentage)
        binding.progressCircular.setProgressCompat(percentage, true)
    }

    override fun onError() {
        binding.progressCircular.apply {
            setProgressCompat(100, true)
            setIndicatorColor(resources.getColor(R.color.deep_orange_A400, null))
        }
        toggleFab(true)
    }

    override fun onFinish(percentage: Int) {
        binding.progressCircular.setProgressCompat(100, true)
        toggleFab(true)
    }

    override fun onResume() {
        super.onResume()
        toggleFab(true, R.color.light_green_A200, R.drawable.ic_round_save_30).setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class FragmentPagerAdapter(fragment: Fragment, val tabs: List<Fragment>) :
        FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = tabs.size

        override fun createFragment(position: Int): Fragment = tabs[position]
    }
}
