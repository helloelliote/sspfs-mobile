/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.map

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
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
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.CameraAnimation.Easing
import com.naver.maps.map.CameraUpdate.*
import com.naver.maps.map.LocationTrackingMode.NoFollow
import com.naver.maps.map.NaverMap.LAYER_GROUP_CADASTRAL
import com.naver.maps.map.NaverMap.MapType.Basic
import com.naver.maps.map.NaverMap.MapType.Satellite
import com.naver.maps.map.overlay.*
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.DelicateCoroutinesApi
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
open class NaverMapFragment : Fragment(), OnMapReadyCallback, MenuProvider {

    val viewModel: FeatureViewModel by activityViewModels { FeatureVMFactory }

    val args: NaverMapFragmentArgs by navArgs()

    // This property is only valid between onCreateView and onDestroyView.
    var _binding: FragmentMapBinding? = null
    val binding get() = _binding!!

    lateinit var executor: ExecutorService
    lateinit var handler: Handler
    lateinit var sharedPref: SharedPreferences
    lateinit var locationSource: FusedLocationSource
    open lateinit var naverMap: NaverMap

    @Suppress("DEPRECATION")
    private val naverMapPadding: Int by lazy {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        return@lazy resources.getDimensionPixelOffset(R.dimen.bottomappbar_height) + (displayMetrics.heightPixels / 20)
    }
    private val markerMap = mutableMapOf<String, Overlay>()
    private val markerOnClickListener = Overlay.OnClickListener { overlay ->
        overlay as Marker
        val feature = overlay.tag as Feature
        val selectOverlay: Overlay
        val bounds: LatLngBounds
        if (arrowheadPathMap.containsKey(feature.fac_uid)) {
            selectOverlay = arrowheadPathMap[feature.fac_uid] as ArrowheadPathOverlay
            arrowheadPathMap[feature.fac_uid]!!.performClick()
            bounds = arrowheadPathMap[feature.fac_uid]!!.bounds.buffer(10.0)
        } else {
            selectOverlay = overlay
            bounds = LatLngBounds(overlay.position, overlay.position).buffer(10.0)
            arrowheadPath?.apply {
                outlineColor = WHITE
                outlineWidth = 2
                width = 5
            }
        }
        viewModel.type(feature.fac_typ).featureGet(feature.fac_uid).observeOnce(viewLifecycleOwner) {
            viewModel.set(it).overlay(selectOverlay).latLngBounds(bounds)
            naverMap.apply {
                naverMap.mapType = Satellite
            }.moveCamera(fitBounds(bounds).animate(Easing, 250).finishCallback {
                val directions = NaverMapFragmentDirections.actionToFeatureFragment(type = feature.fac_typ)
                findNavController().navigate(directions)
            })
        }
        return@OnClickListener true
    }
    private var arrowheadPath: ArrowheadPathOverlay? = null
    private val arrowheadPathMap = mutableMapOf<String, ArrowheadPathOverlay>()
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
    open var pathMarker: Marker? = null
    open var pathOverlay: ArrowheadPathOverlay? = null
    open val pathOverlayList = mutableListOf<LatLng>()

    open lateinit var fab: FloatingActionButton
    open lateinit var bottomAppBar: BottomAppBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
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

    fun onCreateMap() {
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

        if (args.location != null) {
            naverMap.locationTrackingMode = NoFollow
            naverMap.cameraPosition = CameraPosition(args.location!!, naverMap.cameraPosition.zoom)
        } else {
//            naverMap.locationTrackingMode = Follow
            naverMap.locationTrackingMode = NoFollow
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
        bottomAppBar = requireActivity().findViewById(R.id.bottom_app_bar)

        FeatureEdit(
            this.naverMap,
            bottomAppBar,
            fab,
            pathOverlay,
            pathOverlayList
        )

        naverMap.setOnMapLongClickListener { point, coord ->
            bottomAppBar.replaceMenu(R.menu.bottomappbar_menu_fragment_map_add)
            val cameraUpdate = CameraUpdate.scrollTo(coord).animate(CameraAnimation.Linear)
                .finishCallback {
                    when (pathOverlayList.size) {
                        0 -> {
                            pathMarker = Marker(coord, MarkerIcons.BLACK).apply {
                                iconTintColor = Color.WHITE
                                captionText = "시작점"
                                captionColor = Color.WHITE
                                captionHaloColor = BLACK
                                map = naverMap
                            }
                            pathOverlayList.add(coord)
                        }

                        1 -> {
                            pathOverlayList.add(coord)
                            pathOverlay = ArrowheadPathOverlay().apply {
                                headSizeRatio = 3.0f
                                coords = pathOverlayList
                                map = naverMap
                            }
                        }

                        else -> {
                            pathOverlayList.add(coord)
                            pathOverlay?.coords = pathOverlayList
                        }
                    }
                }
            naverMap.moveCamera(cameraUpdate)
        }
    }

    fun onCameraIdle() {
        sharedPref.edit().putFloat("latitude", naverMap.cameraPosition.target.latitude.toFloat())
            .putFloat("longitude", naverMap.cameraPosition.target.longitude.toFloat()).apply()
    }

    fun onFeatureGet() {
        clearOverlays()
        onFeatureGetResult(false)
        val latLngBounds = naverMap.coveringBounds
        viewModel.featuresGet(
            xmin = latLngBounds.westLongitude,
            ymin = latLngBounds.southLatitude,
            xmax = latLngBounds.eastLongitude,
            ymax = latLngBounds.northLatitude,
        ).observeOnce(viewLifecycleOwner) {
            executor.execute {
                it.features.stream().forEach { feature ->
                    val key = feature.fac_uid
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

    fun createMarker(point: LatLng, @ColorInt tintColor: Int, feature: Feature) =
        Marker(point, MarkerIcons.BLACK).apply {
            height = 60
            width = 45
            captionMinZoom = 17.0
            captionTextSize = 14.0f
            captionText = feature.fac_nam
            subCaptionColor = WHITE
            subCaptionHaloColor = BLACK
            subCaptionTextSize = 14.0f
            isHideCollidedSymbols = true
            tag = feature
            onClickListener = markerOnClickListener
            when (feature.exm_chk) {
                EXM_CHK_SAVE -> {
                    iconTintColor = LTGRAY
                    captionColor = WHITE
                    captionHaloColor = BLACK
                    subCaptionText = feature.exm_ymd()
                }

                EXM_CHK_EXCLUDE -> {
                    iconTintColor = GRAY
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

    fun createRegionMarker(point: LatLng, region: Region) =
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

    fun createArrowheadPath(line: List<LatLng>, @ColorInt tintColor: Int, feature: Feature) =
        ArrowheadPathOverlay(line).apply {
            outlineColor = WHITE
            width = 5
            headSizeRatio = 4.5f
            tag = feature
            onClickListener = arrowheadPathOnClickListener
            color = when (feature.exm_chk) {
                EXM_CHK_SAVE -> GRAY
                EXM_CHK_EXCLUDE -> GRAY
                else -> tintColor
            }
        }

    /*
        fun createMultipartPath(lines: List<List<LatLng>>, @ColorInt tintColor: Int) =
            MultipartPathOverlay(lines, List(lines.size) { ColorPart(tintColor, WHITE, WHITE, WHITE) }).apply {
                width = 5
            }
    */

    fun onFeatureGetResult(
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

    fun clearOverlays() {
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

    fun onRegionGet() {
        clearRegionOverlays()
        val latLngBounds = naverMap.coveringBounds
        viewModel.regionsGet(
            xmin = latLngBounds.westLongitude,
            ymin = latLngBounds.southLatitude,
            xmax = latLngBounds.eastLongitude,
            ymax = latLngBounds.northLatitude,
        ).observeOnce(viewLifecycleOwner) {
            executor.execute {
                it.regions.stream().forEach { region ->
                    val latLngs = region.geom.latLngs
                    latLngs.forEach { latLng ->
                        polygonMap[region.bjd_nam] = (createPolygon(coords = latLng).also {
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
    }

    fun createPolygon(coords: List<LatLng>) =
        PolygonOverlay(coords).apply {
            color = resources.getColor(android.R.color.transparent, null)
            outlineWidth = 5
            outlineColor = WHITE
        }

    fun clearRegionOverlays() {
        centerMap.values.stream().forEach {
            if (it is Marker) it.map = null
        }
        centerMap.clear()
        polygonMap.values.stream().forEach {
            if (it is PolygonOverlay) it.map = null
        }
        polygonMap.clear()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.bottomappbar_menu_fragment_map, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            /*            R.id.action_legend -> {
                            NavDrawerFragment().show(childFragmentManager, null)
                            return true
                        }*/
            R.id.action_region -> {
                onRegionGet()
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

            R.id.action_cancel -> {
                bottomAppBar.replaceMenu(R.menu.bottomappbar_menu_fragment_map)
                pathOverlay?.map = null
                pathOverlay?.coords?.clear()
                pathOverlayList.clear()
                pathMarker?.map = null
                return true
            }

            else -> false
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

    private class FeatureEdit(
        override var naverMap: NaverMap,
        override var bottomAppBar: BottomAppBar,
        override var fab: FloatingActionButton,
        override var pathOverlay: ArrowheadPathOverlay?,
        override val pathOverlayList: MutableList<LatLng>,
    ) : NaverMapFragment() {

        init {

        }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}
