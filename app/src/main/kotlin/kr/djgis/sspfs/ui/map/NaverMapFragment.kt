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
import kr.djgis.sspfs.data.FeatureA
import kr.djgis.sspfs.data.FeatureBase
import kr.djgis.sspfs.databinding.FragmentMapBinding
import kr.djgis.sspfs.model.FeatureVMFactory2
import kr.djgis.sspfs.model.FeatureViewModel2
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

    //    private val viewModel: FeatureViewModel by activityViewModels { FeatureVMFactory }
    private val viewModel2: FeatureViewModel2 by activityViewModels { FeatureVMFactory2 }

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
    private val overlayOnClickListener = Overlay.OnClickListener {
        viewModel2.setFeatureA((it as Marker).tag as FeatureA)
        naverMap.apply {
//            it.isVisible = false
            setContentPadding(0, 0, 0, 0)
            naverMap.mapType = Satellite
        }.moveCamera(scrollAndZoomTo(it.position, 18.0).animate(Easing, 250).finishCallback {
            GlobalScope.launch(Dispatchers.Main) {
                naverMap.takeSnapshot(false) { bitmap ->
                    val directions = NaverMapFragmentDirections.actionToFeatureFragment(bitmap)
                    findNavController().navigate(directions)
                }
            }
        })
/*        viewModel.setFeature((it as Marker).tag as Feature)
        naverMap.apply {
//            it.isVisible = false
            setContentPadding(0, 0, 0, 0)
            naverMap.mapType = Satellite
        }.moveCamera(scrollAndZoomTo(it.position, 18.0).animate(Easing, 250).finishCallback {
            GlobalScope.launch(Dispatchers.Main) {
                naverMap.takeSnapshot(false) { bitmap ->
                    val directions = NaverMapFragmentDirections.actionToFeatureFragment(bitmap)
                    findNavController().navigate(directions)
                }
            }
        })*/
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
//            viewModel = this@NaverMapFragment.viewModel
            viewModel2 = this@NaverMapFragment.viewModel2
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
                        this.onFeatureAGet(it)
                    })
                }.show()
            } else onFeatureAGet(it)
        }
    }

    private fun onCameraIdle() {
        sharedPref.edit().putFloat("latitude", naverMap.cameraPosition.target.latitude.toFloat())
            .putFloat("longitude", naverMap.cameraPosition.target.longitude.toFloat()).apply()
    }

/*
    private fun onFeatureGet(view: View) {
        clearOverlays()
        onFeatureGetResult(false)
        val latLngBounds = naverMap.coveringBounds
        viewModel.features(
            xmin = latLngBounds.westLongitude,
            ymin = latLngBounds.southLatitude,
            xmax = latLngBounds.eastLongitude,
            ymax = latLngBounds.northLatitude,
        ).observeOnce(this@NaverMapFragment) {
            executor.execute {
                val pointOverlays = mutableSetOf<Marker>()
                val lineOverlays = mutableSetOf<Overlay>()
                it.features.stream().forEach { feature ->
                    val latLngs = feature.geom.latLngs
                    val color = toColor(feature)
                    when (feature.geom.type) {
                        "Point" -> {
                            pointOverlays.add(createMarker(point = latLngs[0][0], color, feature))
                        }
                        "LineString" -> {
                            lineOverlays.add(createArrowheadPath(line = latLngs[0], color).also {
                                pointOverlays.add(createMarker(point = it.coords[0], color, feature))
                            })
                        }
                        "MultiLineString" -> {
                            latLngs.forEach { latLng ->
                                lineOverlays.add(createArrowheadPath(line = latLng, color).also {
                                    pointOverlays.add(createMarker(point = it.coords[0], color, feature))
                                })
                            }
                        }
                    }
                }
                handler.post {
                    lineOverlays.stream().forEach { it.map = naverMap }
                    pointOverlays.stream().forEach { it.map = naverMap }
                    onFeatureGetResult(true, R.color.teal_A400)
                    if (it.featureCount == 0) {
                        snackbar(fab, R.string.map_feature_get_empty).setAction("확인") {}.show()
                    }
                }
                // (Archive) For "MultiLineString": MultipartPathOverlay
                */
/*
                val multiLineStrings = mutableListOf<MultipartPathOverlay>()
                val pointMarkers = mutableListOf<Marker>()
                it.features.forEach { feature ->
                    val lineStrings = mutableListOf<List<LatLng>>()
                    feature.geom?.forEach { points ->
                        val lineString = mutableListOf<LatLng>()
                        points.asJsonArray.forEach { point ->
                            lineString.add(LatLng(
                                point.asJsonArray[1].asDouble,
                                point.asJsonArray[0].asDouble,
                            ))
                        }
                        lineStrings.add(lineString)
                    }
                    multiLineStrings.add(MultipartPathOverlay().apply {
                        coordParts = lineStrings
                        colorParts = List(lineStrings.size) {
                            ColorPart(BLACK, BLACK, BLACK, BLACK)
                        }
                        isHideCollidedSymbols = true
//                            lineStringMap[feature.id] = this
                    }.also {
                        val markerLatLng: LatLng = it.coordParts.first().first()
                        pointMarkers.add(Marker(markerLatLng).apply {
                            tag = feature
                            onClickListener = overlayOnClickListener
                        })
//                            markerMap[feature.id] = pointMarker
                    })
                }
                handler.post {
                    multiLineStrings.forEach { it.map = naverMap }
                    pointMarkers.forEach { it.map = naverMap }
                    onFeatureGetResult(true, R.color.teal_A400)
                    if (it.featureCount == 0) {
                        snackbar(fab, R.string.map_feature_get_empty).setAction("확인") {}.show()
                    }
                }
                *//*

                // (Archive) For "Point"
                */
/*
                val pointMarkers = mutableListOf<Marker>()
                it.features.forEach { feature ->
                    pointMarkers.add(Marker(feature.latLng).apply {
                        tag = feature
                        onClickListener = overlayOnClickListener
                        height = 80
                        width = 60
                        icon = MarkerIcons.BLACK
                        iconTintColor = toColor(feature)
                        isHideCollidedMarkers = false
                        isHideCollidedSymbols = true
                    })
                }
                handler.post {
                    pointMarkers.forEach { it.map = naverMap }
                    onFeatureGetResult(true, R.color.teal_A400)
                    if (it.featureCount == 0) {
                        snackbar(fab, R.string.map_feature_get_empty).setAction("확인") {}.show()
                    }
                }
                *//*

            }
        }
    }
*/

    private fun createMarker(point: LatLng, @ColorInt tintColor: Int, feature: FeatureBase) =
        Marker(point, MarkerIcons.BLACK).apply {
            height = 60
            width = 45
            iconTintColor = tintColor
            captionColor = tintColor
            captionHaloColor = if (tintColor == RED || tintColor == BLUE) WHITE else BLACK
            captionMinZoom = 18.0
            captionTextSize = 13.0f
//            captionText = feature.id.drop(2)
            captionText = (feature as FeatureA).fac_nam.toString()
            isHideCollidedSymbols = true
            tag = feature
            onClickListener = overlayOnClickListener
        }

    private fun createArrowheadPath(line: List<LatLng>, @ColorInt tintColor: Int) =
        ArrowheadPathOverlay(line).apply {
            color = tintColor
            outlineColor = WHITE
            width = 5
            headSizeRatio = 4.5f
        }

    /*
        private fun createMultipartPath(lines: List<List<LatLng>>, @ColorInt tintColor: Int) =
            MultipartPathOverlay(lines, List(lines.size) { ColorPart(tintColor, WHITE, WHITE, WHITE) }).apply {
                width = 5
            }
    */

    private fun onFeatureAGet(view: View) {
        clearOverlays()
        onFeatureGetResult(false)
        val latLngBounds = naverMap.coveringBounds
        viewModel2.featuresA(
            xmin = latLngBounds.westLongitude,
            ymin = latLngBounds.southLatitude,
            xmax = latLngBounds.eastLongitude,
            ymax = latLngBounds.northLatitude,
        ).observeOnce(this@NaverMapFragment) {
            executor.execute {
                val pointOverlays = mutableSetOf<Marker>()
                val lineOverlays = mutableSetOf<Overlay>()
                it.features.stream().forEach { featureA ->
                    val latLngs = featureA.geom.latLngs
                    val color = if (featureA.exm_ymd == null) RED else LTGRAY
                    pointOverlays.add(createMarker(point = latLngs[0][0], color, featureA))
                }
                handler.post {
                    lineOverlays.stream().forEach { it.map = naverMap }
                    pointOverlays.stream().forEach { it.map = naverMap }
                    onFeatureGetResult(true, R.color.teal_A400)
                    if (it.featureCount == 0) {
                        snackbar(fab, R.string.map_feature_get_empty).setAction("확인") {}.show()
                    }
                }
            }
        }
    }

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
