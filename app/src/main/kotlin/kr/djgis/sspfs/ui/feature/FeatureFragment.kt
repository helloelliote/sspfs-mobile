/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.feature

import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.JsonElement
import kotlinx.coroutines.DelicateCoroutinesApi
import kr.djgis.sspfs.Config.EXM_CHK_EXCLUDE
import kr.djgis.sspfs.Config.EXM_CHK_SAVE
import kr.djgis.sspfs.R
import kr.djgis.sspfs.databinding.FragmentFeatureBinding
import kr.djgis.sspfs.model.FeatureVMFactory
import kr.djgis.sspfs.model.FeatureViewModel
import kr.djgis.sspfs.network.RetrofitProgress
import kr.djgis.sspfs.ui.MainActivity
import kr.djgis.sspfs.ui.feature.tabs.*
import kr.djgis.sspfs.util.alertDialog
import kr.djgis.sspfs.util.glide
import kr.djgis.sspfs.util.observeOnce
import kr.djgis.sspfs.util.toggleFab
import okhttp3.MultipartBody
import okhttp3.MultipartBody.Part.Companion.createFormData

@DelicateCoroutinesApi
class FeatureFragment : Fragment(), View.OnClickListener, RetrofitProgress.MultipartUploadCallback {

    private val viewModel: FeatureViewModel by activityViewModels { FeatureVMFactory }

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentFeatureBinding? = null
    private val binding get() = _binding!!

    private val args: FeatureFragmentArgs by navArgs()

    private var adapter: FragmentPagerAdapter? = null

    private val dpWidth: Boolean by lazy {
        val displayMetrics = resources.displayMetrics
        return@lazy (displayMetrics.widthPixels / displayMetrics.density) > 600.0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isEnabled = false
                activity?.onBackPressed()
            }
        })

        adapter = when (args.type) {
            "A" -> FragmentPagerAdapter(
                this@FeatureFragment, listOf(
                    FeatureAEdit1(),
                    FeatureAEdit2(),
                    FeatureImage(args.type, args.pos!!),
                )
            )

            "B" -> {
                FragmentPagerAdapter(
                    this@FeatureFragment, when (args.pos) {
                        "l" -> listOf(
                            FeatureBEdit1L(),
                            FeatureBEdit2L(),
//                            FeatureImage(args.type!!, args.pos!!),
                        )

                        "m" -> listOf(
                            FeatureBEdit1U(),
                            FeatureBEdit2U(),
                            FeatureBEdit1M(),
                            FeatureBEdit2M(),
                            FeatureBEdit1L(),
                            FeatureBEdit2L(),
                            FeatureImage(args.type, args.pos!!),
                        )

                        "u" -> listOf(
                            FeatureBEdit1U(),
                            FeatureBEdit2U(),
//                            FeatureImage(args.type!!, args.pos!!),
                        )

                        else -> listOf()
                    }
                )
            }

            "C" -> FragmentPagerAdapter(
                this@FeatureFragment, listOf(
                    FeatureCEdit1(),
                    FeatureCEdit2(),
                    FeatureImage(args.type, args.pos!!),
                )
            )

            "D" -> FragmentPagerAdapter(
                this@FeatureFragment, listOf(
                    FeatureDEdit1(),
                    FeatureDEdit2(),
                    FeatureImage(args.type, args.pos!!),
                )
            )

            "E" -> {
                FragmentPagerAdapter(
                    this@FeatureFragment, when (args.pos) {
                        "l" -> listOf(
                            FeatureEEdit1L(),
                            FeatureEEdit2L(),
//                            FeatureImage(args.type!!, args.pos!!),
                        )

                        "m" -> listOf(
                            FeatureEEdit1U(),
                            FeatureEEdit2U(),
                            FeatureEEdit1M(),
                            FeatureEEdit2M(),
                            FeatureEEdit1L(),
                            FeatureEEdit2L(),
                            FeatureImage(args.type, args.pos!!),
                        )

                        "u" -> listOf(
                            FeatureEEdit1U(),
                            FeatureEEdit2U(),
//                            FeatureImage(args.type!!, args.pos!!),
                        )

                        else -> listOf()
                    }
                )
            }

            "F" -> {
                FragmentPagerAdapter(
                    this@FeatureFragment, when (args.pos) {
                        "l" -> listOf(
                            FeatureFEdit1L(),
                            FeatureFEdit2L(),
//                            FeatureImage(args.type!!, args.pos!!),
                        )

                        "m" -> listOf(
                            FeatureFEdit1U(),
                            FeatureFEdit2U(),
                            FeatureFEdit1M(),
                            FeatureFEdit2M(),
                            FeatureFEdit1L(),
                            FeatureFEdit2L(),
                            FeatureImage(args.type, args.pos!!),
                        )

                        "u" -> listOf(
                            FeatureFEdit1U(),
                            FeatureFEdit2U(),
//                            FeatureImage(args.type!!, args.pos!!),
                        )

                        else -> listOf()
                    }
                )
            }

            else -> FragmentPagerAdapter(this@FeatureFragment, listOf())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFeatureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            listOf(appbarLayout, collapsingToolbarLayout).forEach {
                it.layoutParams.height = if (dpWidth) 800 else 800
            }
        }

        binding.run {
            viewPager.isUserInputEnabled = false
            viewPager.adapter = adapter
            TabLayoutMediator(tabs, viewPager) { tab, position ->
                tab.apply {
                    val tabItem = (viewPager.adapter as FragmentPagerAdapter).tabs[position] as FeatureTabsInterface
                    text = tabItem.text
                    icon = ResourcesCompat.getDrawable(resources, tabItem.iconDrawable, null)!!
                }
            }.attach()

            (requireActivity() as MainActivity).setSupportActionBar(toolbar)
            viewModel.getBitmap().observeOnce(this@FeatureFragment) { glide(it).into(backdrop) }
        }
    }

    private fun onSave(chk: String, observer: Observer<JsonElement>) {
        val parts = mutableListOf<MultipartBody.Part>()
        viewModel.of(args.type).value?.apply {
            img_fac!!.forEach { attachment ->
                if (attachment.uri == null) {
                    return@forEach
                } else {
                    val part = createFormData(
                        "files",
                        attachment.name,
                        RetrofitProgress(requireContext(), attachment.uri!!, "image", this@FeatureFragment)
                    )
                    parts.add(part)
                }
            }
            exm_chk = chk
        }
        viewModel.featurePost(args.type, parts).observeOnce(this@FeatureFragment, observer)
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_remove -> {
                alertDialog(
                    title = viewModel.of(args.type).value!!.fac_nam,
                    message = resources.getString(R.string.feature_exclude)
                ).setNegativeButton("취소") { dialog, which ->
                }.setPositiveButton("제외") { dialog, which ->
                    onSave(EXM_CHK_EXCLUDE) {
                        requireActivity().onBackPressed()
                    }
                }.show()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.fab_main -> {
                onSave(EXM_CHK_SAVE) {
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    override fun onInitiate(percentage: Int) {
        toggleFab(false, R.color.light_green_A200, R.drawable.ic_round_save_30)
        binding.progressCircular.setProgressCompat(percentage, true)
    }

    override fun onProgress(percentage: Int) {
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
}
