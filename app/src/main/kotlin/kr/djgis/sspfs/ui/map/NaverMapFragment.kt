/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.map

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color.*
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
import com.naver.maps.map.CameraUpdate.*
import com.naver.maps.map.LocationTrackingMode.NoFollow
import com.naver.maps.map.NaverMap.LAYER_GROUP_CADASTRAL
import com.naver.maps.map.NaverMap.MapType.Basic
import com.naver.maps.map.NaverMap.MapType.Satellite
import com.naver.maps.map.overlay.ArrowheadPathOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.PolygonOverlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kr.djgis.sspfs.Config.EXM_CHK_EXCLUDE
import kr.djgis.sspfs.Config.EXM_CHK_SAVE
import kr.djgis.sspfs.Config.EXTENT_GYEONGJU
import kr.djgis.sspfs.Config.LATLNG_GYEONGJU
import kr.djgis.sspfs.R
import kr.djgis.sspfs.data.*
import kr.djgis.sspfs.data.FeatureType.Companion.toColor
import kr.djgis.sspfs.databinding.FragmentMapBinding
import kr.djgis.sspfs.model.FeatureVMFactory
import kr.djgis.sspfs.model.FeatureViewModel
import kr.djgis.sspfs.util.observeOnce
import kr.djgis.sspfs.util.snackbar
import kr.djgis.sspfs.util.toggleFab
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

    @Suppress("DEPRECATION")
    private val naverMapPadding: Int by lazy {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        return@lazy resources.getDimensionPixelOffset(R.dimen.bottomappbar_height) + (displayMetrics.heightPixels / 20)
    }
    private var marker: Marker? = null
    private val markerMap = mutableMapOf<String, Overlay>()
    private val markerOnClickListener = Overlay.OnClickListener { overlay ->
        overlay as Marker
        marker?.apply {
        }.also {
            marker = overlay.apply {
                map = naverMap
            }
        }
        val feature = overlay.tag as Feature
        if (arrowheadPathMap.containsKey(feature.fac_uid)) {
            arrowheadPathMap[feature.fac_uid]?.performClick()
        } else {
            arrowheadPath?.apply {
                outlineColor = WHITE
                outlineWidth = 2
                width = 5
            }
        }
        viewModel.featureGet(feature.fac_typ!!, feature.fac_uid!!).observeOnce(this@NaverMapFragment) { _feature ->
            when (feature.fac_typ) {
                "A" -> viewModel.setCurrentFeature(_feature as FeatureA)
                "B" -> viewModel.setCurrentFeature(_feature as FeatureB)
                "C" -> viewModel.setCurrentFeature(_feature as FeatureC)
                "D" -> viewModel.setCurrentFeature(_feature as FeatureD)
                "E" -> viewModel.setCurrentFeature(_feature as FeatureE)
                "F" -> viewModel.setCurrentFeature(_feature as FeatureF)
            }
            naverMap.apply {
                setContentPadding(0, 0, 0, 0)
                naverMap.mapType = Satellite
            }.moveCamera(scrollAndZoomTo(
                overlay.position,
                if (naverMap.cameraPosition.zoom > 18.0) naverMap.cameraPosition.zoom else 18.0
            ).animate(Easing, 250).finishCallback {
                GlobalScope.launch(Dispatchers.Main) {
                    naverMap.takeSnapshot(false) { bitmap ->
                        viewModel.setBitmap(bitmap)
                        val directions = NaverMapFragmentDirections.actionToFeatureFragment(
                            type = feature.fac_typ!!, pos = "m"
                        )
                        findNavController().navigate(directions)
                    }
                }
            })
        }
        return@OnClickListener true
    }
    private var arrowheadPath: ArrowheadPathOverlay? = null
    private val arrowheadPathMap = mutableMapOf<String, Overlay>()
    private val arrowheadPathOnClickListener = Overlay.OnClickListener { overlay ->
        overlay as ArrowheadPathOverlay
        arrowheadPath?.apply {
            outlineColor = WHITE
            outlineWidth = 2
            width = 5
        }.also {
            arrowheadPath = overlay.apply {
                outlineColor = BLACK
                outlineWidth = 4
                width = 10
                map = naverMap
            }
        }
        return@OnClickListener true
    }

    private val polygonMap = mutableMapOf<String, PolygonOverlay>()
    private val centerMap = mutableMapOf<String, Marker>()

    private val centerOnClickListener = Overlay.OnClickListener { overlay ->
        overlay as Marker
        polygonMap.values.stream().forEach {
            it.zIndex = 0
            it.outlineColor = WHITE
        }
        val polygon = polygonMap[overlay.tag as String]
        polygon?.let {
            it.zIndex = 1
            it.outlineColor = RED
            naverMap.moveCamera(fitBounds(it.bounds.buffer(100.0)).animate(Easing, 250))
        }
        return@OnClickListener true
    }

    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        registerForActivityResult(RequestPermission()) {}.launch(ACCESS_FINE_LOCATION)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        executor = Executors.newCachedThreadPool()
        handler = Handler(Looper.getMainLooper())
        sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
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
        naverMap.locationSource = locationSource
        naverMap.addOnCameraIdleListener { onCameraIdle() }

        args.location?.let {
            naverMap.locationTrackingMode = NoFollow
            naverMap.cameraPosition = CameraPosition(it, naverMap.cameraPosition.zoom)
        }

        fab = requireActivity().findViewById(R.id.fab_main)
        fab.setOnClickListener {
            if (this.naverMap.cameraPosition.zoom < 13.0) {
                snackbar(fab, R.string.map_require_zoom).setAction("확대") {
                    this.naverMap.moveCamera(zoomTo(14.0).animate(Easing).finishCallback {
                        this.onFeatureGet()
                    })
                }.show()
            } else onFeatureGet()
        }
    }

    private fun onCameraIdle() {
        sharedPref.edit().putFloat("latitude", naverMap.cameraPosition.target.latitude.toFloat())
            .putFloat("longitude", naverMap.cameraPosition.target.longitude.toFloat()).apply()
    }

    private fun onFeatureGet() {
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
                it.features.stream().forEach { feature ->
                    val key = feature.fac_uid!!
                    val latLngs = feature.geom!!.latLngs
                    val color = toColor(feature)
                    when (feature.geom!!.type) {
                        "Point" -> {
                            markerMap[key] = createMarker(point = latLngs[0][0], color, feature)
                        }

                        "LineString" -> {
                            arrowheadPathMap[key] = createArrowheadPath(line = latLngs[0], color, feature).also {
                                markerMap[key] = createMarker(point = it.coords[1], color, feature)
//                                markerSet.add(createMarker(point = it.coords[1], color, feature))
                            }
                        }

                        "MultiLineString" -> {
                            latLngs.forEach { latLng ->
                                arrowheadPathMap[key] = createArrowheadPath(line = latLng, color, feature).also {
                                    markerMap[key] = createMarker(point = it.coords[0], color, feature)
                                }
                            }
                        }
                    }
                }
                handler.post {
                    arrowheadPathMap.values.stream().forEach {
                        it.map = naverMap
                    }
                    markerMap.values.stream().forEach {
                        it.map = naverMap
                    }
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
            captionMinZoom = 17.0
            captionTextSize = 14.0f
            captionText = feature.fac_nam!!
            subCaptionColor = WHITE
            subCaptionHaloColor = BLACK
            subCaptionTextSize = 14.0f
            isHideCollidedSymbols = true
            tag = feature
            onClickListener = markerOnClickListener
            when (feature.exm_chk) {
                EXM_CHK_SAVE -> {
                    icon = MarkerIcons.BLACK
                    iconTintColor = WHITE
                    captionColor = WHITE
                    captionHaloColor = BLACK
                    subCaptionText = feature.exm_ymd()
                }

                EXM_CHK_EXCLUDE -> {
                    icon = MarkerIcons.GRAY
                    captionColor = WHITE
                    captionHaloColor = BLACK
                    subCaptionText = "선정제외"
                }

                else -> {
                    iconTintColor = tintColor
                    captionColor = tintColor
                    captionHaloColor = if (tintColor == RED || tintColor == BLUE) WHITE else BLACK
                }
            }
        }

    private fun createRegionMarker(point: LatLng, region: Region) =
        Marker(point).apply {
            width = 1
            height = 1
            captionMinZoom = 12.0
            captionTextSize = 12.0f
            captionText = region.hjd_nam
            captionColor = WHITE
            captionHaloColor = BLACK
            subCaptionTextSize = 16.0f
            subCaptionText = region.bjd_nam
            subCaptionColor = WHITE
            subCaptionHaloColor = BLACK
            isHideCollidedSymbols = true
            tag = region.bjd_nam
            onClickListener = centerOnClickListener
        }

    private fun createArrowheadPath(line: List<LatLng>, @ColorInt tintColor: Int, feature: Feature) =
        ArrowheadPathOverlay(line).apply {
            outlineColor = WHITE
            width = 5
            headSizeRatio = 4.5f
            tag = feature
            onClickListener = arrowheadPathOnClickListener
            when (feature.exm_chk) {
                EXM_CHK_SAVE -> {
                    color = GRAY
                }

                EXM_CHK_EXCLUDE -> {
                    color = GRAY
                }

                else -> {
                    color = tintColor
                }
            }
        }

    private fun createPolygon(coords: List<LatLng>, region: Region) =
        PolygonOverlay(coords).apply {
            color = resources.getColor(android.R.color.transparent, null)
            outlineWidth = 5
            outlineColor = WHITE
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
        toggleFab(isEnabled, colorInt, drawableInt)
    }

    private fun clearOverlays() {
        marker?.map = null
        arrowheadPath?.map = null
        markerMap.values.stream().forEach {
            if (it is Overlay) it.map = null
        }
        markerMap.clear()
        arrowheadPathMap.values.stream().forEach {
            if (it is Overlay) it.map = null
        }
        arrowheadPathMap.clear()
    }

    private fun clearRegionOverlays() {
        centerMap.values.stream().forEach {
            if (it is Marker) it.map = null
        }
        centerMap.clear()
        polygonMap.values.stream().forEach {
            if (it is PolygonOverlay) it.map = null
        }
        polygonMap.clear()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bottomappbar_menu_fragment_map, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            /*            R.id.action_legend -> {
                            NavDrawerFragment().show(childFragmentManager, null)
                            return true
                        }*/
            R.id.action_region -> {
                clearRegionOverlays()
                val latLngBounds = naverMap.coveringBounds
                viewModel.regionsGet(
                    xmin = latLngBounds.westLongitude,
                    ymin = latLngBounds.southLatitude,
                    xmax = latLngBounds.eastLongitude,
                    ymax = latLngBounds.northLatitude,
                ).observeOnce(this@NaverMapFragment) {
                    executor.execute {
                        it.regions.stream().forEach { region ->
                            val latLngs = region.geom.latLngs
                            latLngs.forEach { latLng ->
                                polygonMap[region.bjd_nam] = (createPolygon(coords = latLng, region = region).also {
                                    val centerLatlng = region.center.latLngs
                                    centerMap[region.bjd_nam] = createRegionMarker(centerLatlng[0][0], region)
                                })
                            }
                        }
                        handler.post {
                            polygonMap.values.stream().forEach {
                                it.map = naverMap
                            }
                            centerMap.values.stream().forEach {
                                it.map = naverMap
                            }
                        }
                    }
                }
                snackbar(fab, R.string.map_region).setAction("확인") {}.show()
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
        toggleFab(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}
