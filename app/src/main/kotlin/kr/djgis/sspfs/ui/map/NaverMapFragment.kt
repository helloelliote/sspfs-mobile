/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.map

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color.*
import android.graphics.PointF
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.CameraAnimation.Easing
import com.naver.maps.map.CameraUpdate.scrollAndZoomTo
import com.naver.maps.map.CameraUpdate.zoomTo
import com.naver.maps.map.LocationTrackingMode.NoFollow
import com.naver.maps.map.LocationTrackingMode.None
import com.naver.maps.map.NaverMap.LAYER_GROUP_CADASTRAL
import com.naver.maps.map.NaverMap.MapType.Basic
import com.naver.maps.map.NaverMap.MapType.Satellite
import com.naver.maps.map.overlay.ArrowheadPathOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kr.djgis.sspfs.Config.EXTENT_GYEONGJU
import kr.djgis.sspfs.Config.LATLNG_GYEONGJU
import kr.djgis.sspfs.R
import kr.djgis.sspfs.data.*
import kr.djgis.sspfs.data.FeatureType.Companion.toColor
import kr.djgis.sspfs.databinding.FragmentMapBinding
import kr.djgis.sspfs.model.FeatureVMFactory
import kr.djgis.sspfs.model.FeatureViewModel
import kr.djgis.sspfs.ui.NavDrawerFragment
import kr.djgis.sspfs.util.observeOnce
import kr.djgis.sspfs.util.screenSize
import kr.djgis.sspfs.util.snackbar
import kr.djgis.sspfs.util.toggle
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@DelicateCoroutinesApi
class NaverMapFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: FeatureViewModel by activityViewModels { FeatureVMFactory }

    private val args: NaverMapFragmentArgs by navArgs()

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var executor: ExecutorService
    private lateinit var handler: Handler
    private lateinit var sharedPref: SharedPreferences
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private val requestPermissionLauncher = registerForActivityResult(RequestPermission()) {
        naverMap.locationTrackingMode = if (it) NoFollow else None
    }
    private val naverMapPadding: Int by lazy {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        return@lazy resources.getDimensionPixelOffset(R.dimen.bottomappbar_height) + (displayMetrics.heightPixels / 20)
    }
    private val arrowheadPathMap = mutableMapOf<String?, Overlay?>()
    private var selectArrowheadPath: ArrowheadPathOverlay? = null
    private val arrowheadPathOnClickListener = Overlay.OnClickListener { overlay ->
        overlay as ArrowheadPathOverlay
        selectArrowheadPath?.apply {
            outlineColor = WHITE
            outlineWidth = 2
            width = 5
        }.also {
            selectArrowheadPath = overlay.apply {
                outlineColor = BLACK
                outlineWidth = 8
                width = 10
            }
        }
        return@OnClickListener true
    }
    private var selectMarker: Marker? = null
    private val markerOnClickListener = Overlay.OnClickListener { overlay ->
        overlay as Marker
        selectMarker?.apply {
            height /= 2
            width /= 2
        }.also {
            selectMarker = overlay.apply {
                height *= 2
                width *= 2
            }
        }
        val feature = overlay.tag as Feature
        arrowheadPathMap[feature.fac_uid]?.performClick()
        viewModel.featureGet(feature.fac_typ!!, feature.fac_uid!!).observeOnce(this@NaverMapFragment) {
            when (feature.fac_typ) {
                "A" -> viewModel.setCurrentFeature(it as FeatureA)
                "B" -> viewModel.setCurrentFeature(it as FeatureB)
                "C" -> viewModel.setCurrentFeature(it as FeatureC)
                "D" -> viewModel.setCurrentFeature(it as FeatureD)
                "E" -> viewModel.setCurrentFeature(it as FeatureE)
                "F" -> viewModel.setCurrentFeature(it as FeatureF)
            }
            overlay.apply {
                globalZIndex -= 1
                tag = it
            }.run {
                infoWindow?.open(this)
            }
            naverMap.apply {
//            it.isVisible = false
                setContentPadding(0, 0, 0, 0)
                naverMap.mapType = Satellite
            }.moveCamera(scrollAndZoomTo(overlay.position, 18.0).animate(Easing, 250).finishCallback {
                GlobalScope.launch(Dispatchers.Main) {
                    naverMap.takeSnapshot(false) { bitmap ->
                        val directions = NaverMapFragmentDirections.actionToFeatureFragment(
                            type = feature.fac_typ!!, bitmap = bitmap
                        )
                        findNavController().navigate(directions)
                    }
                }
            })
        }
        return@OnClickListener true
    }
    private val screenSize by lazy { screenSize() }

    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        executor = Executors.newCachedThreadPool()
        handler = Handler(Looper.getMainLooper())
        sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@NaverMapFragment.viewModel
        }
        onCreateMap()
    }

    private fun onCreateMap() {
        val latLng = if (sharedPref.contains("latitude") && sharedPref.contains("longitude"))
            LatLng(
                sharedPref.getFloat("latitude", 0.0F).toDouble(),
                sharedPref.getFloat("longitude", 0.0F).toDouble()
            ) else LATLNG_GYEONGJU

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance(
                NaverMapOptions()
                    .locale(Locale.KOREA)
                    .mapType(Satellite)
                    .contentPadding(0, 0, 0, naverMapPadding)
                    .camera(CameraPosition(latLng, 16.0))
                    .minZoom(10.0)
                    .extent(EXTENT_GYEONGJU)
                    .locationButtonEnabled(true)
                    .tiltGesturesEnabled(false)
                    .msaaEnabled(true)
                    .symbolScale(1.5f)
            ).also {
                childFragmentManager.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        naverMap.locationSource = locationSource
        naverMap.addOnCameraIdleListener { onCameraIdle() }

        args.location?.let {
            naverMap.locationTrackingMode = NoFollow
            naverMap.cameraPosition = CameraPosition(it, naverMap.cameraPosition.zoom)
        }

        fab = requireActivity().findViewById(R.id.fab_main)
        fab.setOnClickListener {
            if (this.naverMap.cameraPosition.zoom < 14.0) {
                snackbar(fab, R.string.map_require_zoom).setAction("확대") {
                    this.naverMap.moveCamera(zoomTo(14.0).animate(Easing).finishCallback {
                        this.onFeatureGet(it)
                    })
                }.show()
            } else onFeatureGet(it)
        }
    }

    private fun onCameraIdle() {
        sharedPref.edit().putFloat("latitude", naverMap.cameraPosition.target.latitude.toFloat())
            .putFloat("longitude", naverMap.cameraPosition.target.longitude.toFloat()).apply()
    }

    private fun onFeatureGet(view: View) {
        clearOverlays()
        onFeatureGetResult(false)
        val latLngBounds = naverMap.coveringBounds
        viewModel.featuresGet(
            xmin = latLngBounds.westLongitude,
            ymin = latLngBounds.southLatitude,
            xmax = latLngBounds.eastLongitude,
            ymax = latLngBounds.northLatitude,
        ).observeOnce(this@NaverMapFragment) {
            executor.execute {
                val pointOverlays = mutableSetOf<Marker>()
                val lineOverlays = mutableSetOf<Overlay>()
                it.features.stream().forEach { feature ->
                    val latLngs = feature.geom!!.latLngs
                    val color = toColor(feature)
                    when (feature.geom!!.type) {
                        "Point" -> {
                            pointOverlays.add(createMarker(point = latLngs[0][0], color, feature))
                        }
                        "LineString" -> {
                            lineOverlays.add(createArrowheadPath(line = latLngs[0], color, feature).also {
                                pointOverlays.add(createMarker(point = it.coords[1], color, feature))
                            })
                        }
                        "MultiLineString" -> {
                            latLngs.forEach { latLng ->
                                lineOverlays.add(createArrowheadPath(line = latLng, color, feature).also {
                                    pointOverlays.add(createMarker(point = it.coords[0], color, feature))
                                })
                            }
                        }
                    }
                }
                handler.post {
                    lineOverlays.stream().forEach {
                        it.map = naverMap
                        arrowheadPathMap[it.tag as String] = it
                    }
                    pointOverlays.stream().forEach { it.map = naverMap }
                    onFeatureGetResult(true, R.color.teal_A400)
                    if (it.featureCount == 0) {
                        snackbar(fab, R.string.map_feature_get_empty).setAction("확인") {}.show()
                    }
                }
            }
        }
    }

    private fun createMarker(point: LatLng, @ColorInt tintColor: Int, feature: Feature) =
        Marker(point, MarkerIcons.BLACK).apply {
            height = 60
            width = 45
            iconTintColor = tintColor
            captionColor = tintColor
            captionHaloColor = if (tintColor == RED || tintColor == BLUE) WHITE else BLACK
            captionMinZoom = 18.0
            captionTextSize = 14.0f
            captionText = feature.fac_nam
            subCaptionText = feature.exm_ymd()
            subCaptionColor = WHITE
            subCaptionHaloColor = BLACK
            subCaptionTextSize = 14.0f
            isHideCollidedSymbols = true
            tag = feature
            onClickListener = markerOnClickListener
        }

    private fun createArrowheadPath(line: List<LatLng>, @ColorInt tintColor: Int, feature: Feature) =
        ArrowheadPathOverlay(line).apply {
            color = tintColor
            outlineColor = WHITE
            width = 5
            headSizeRatio = 4.5f
            tag = feature.fac_uid
            onClickListener = arrowheadPathOnClickListener
        }

    /*
        private fun createMultipartPath(lines: List<List<LatLng>>, @ColorInt tintColor: Int) =
            MultipartPathOverlay(lines, List(lines.size) { ColorPart(tintColor, WHITE, WHITE, WHITE) }).apply {
                width = 5
            }
    */

    private fun onFeatureGetResult(
        isEnabled: Boolean, @ColorRes colorInt: Int? = null, @DrawableRes drawableInt: Int? = null,
    ) {
        naverMap.uiSettings.apply {
            this.setAllGesturesEnabled(isEnabled)
            isLocationButtonEnabled = isEnabled
            isZoomControlEnabled = isEnabled
            isCompassEnabled = isEnabled
        }
        fab.toggle(isEnabled, colorInt, drawableInt)
    }

    private fun clearOverlays() {
        executor.execute {
            val overlays = naverMap.pickAll(PointF(0.5f, 0.5f), screenSize)
            handler.post {
                overlays.stream().forEach {
                    if (it is Overlay) it.map = null
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bottomappbar_menu_fragment_map, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_legend -> {
                NavDrawerFragment().show(childFragmentManager, null)
                return true
            }
            R.id.action_maptype -> {
                naverMap.mapType = if (naverMap.mapType == Satellite) Basic else Satellite
                return true
            }
            R.id.action_layergroup -> {
                naverMap.setLayerGroupEnabled(
                    LAYER_GROUP_CADASTRAL,
                    !naverMap.isLayerGroupEnabled(LAYER_GROUP_CADASTRAL)
                )
                return true
            }
            R.id.action_search -> {
                val directions = NaverMapFragmentDirections.actionToPlaceSearchFragment(naverMap.cameraPosition.target)
                findNavController().navigate(directions)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        if (::naverMap.isInitialized) naverMap.setContentPadding(0, 0, 0, naverMapPadding)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}
