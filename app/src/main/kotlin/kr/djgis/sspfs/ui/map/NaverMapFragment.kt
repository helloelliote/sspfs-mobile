/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.map

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color.*
import android.graphics.PointF
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.*
import android.view.View.*
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.CameraAnimation.Easing
import com.naver.maps.map.CameraAnimation.Linear
import com.naver.maps.map.CameraUpdate.*
import com.naver.maps.map.LocationTrackingMode.NoFollow
import com.naver.maps.map.NaverMap.LAYER_GROUP_CADASTRAL
import com.naver.maps.map.NaverMap.MapType.Basic
import com.naver.maps.map.NaverMap.MapType.Satellite
import com.naver.maps.map.NaverMap.OnMapLongClickListener
import com.naver.maps.map.overlay.*
import com.naver.maps.map.overlay.OverlayImage.fromResource
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import kr.djgis.sspfs.BuildConfig.GIT_VERSION_NAME
import kr.djgis.sspfs.Config.EXM_CHK_EXCLUDE
import kr.djgis.sspfs.Config.EXM_CHK_SAVE
import kr.djgis.sspfs.Config.EXTENT_GYEONGJU
import kr.djgis.sspfs.Config.LATLNG_GYEONGJU
import kr.djgis.sspfs.R
import kr.djgis.sspfs.data.*
import kr.djgis.sspfs.data.FeatureType.Companion.toColor
import kr.djgis.sspfs.databinding.FragmentMapBinding
import kr.djgis.sspfs.model.FeatureEditVMFactory
import kr.djgis.sspfs.model.FeatureEditViewModel
import kr.djgis.sspfs.model.FeatureVMFactory
import kr.djgis.sspfs.model.FeatureViewModel
import kr.djgis.sspfs.network.RetrofitClient.webService
import kr.djgis.sspfs.util.observeOnce
import kr.djgis.sspfs.util.snackbar
import kr.djgis.sspfs.util.toggleFab
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.set

@DelicateCoroutinesApi
@Suppress("PropertyName")
open class NaverMapFragment : Fragment(), OnMapReadyCallback, MenuProvider {

    private val viewModel: FeatureViewModel by activityViewModels { FeatureVMFactory }
    private val editViewModel: FeatureEditViewModel by activityViewModels { FeatureEditVMFactory }

    private val args: NaverMapFragmentArgs by navArgs()

    // This property is only valid between onCreateView and onDestroyView.
    var _binding: FragmentMapBinding? = null
    val binding get() = _binding!!

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
    private val markerMap = mutableMapOf<String, Overlay>()
    private val markerOnClickListener = Overlay.OnClickListener { overlay ->
        overlay as Marker
        val feature = overlay.tag as Feature
        val selectOverlay: Overlay
        val bounds: LatLngBounds
        if (arrowheadPathMap.containsKey(feature.fac_uid)) {
            selectOverlay = arrowheadPathMap[feature.fac_uid] as ArrowheadPathOverlay
            arrowheadPathMap[feature.fac_uid]!!.performClick()
            bounds = arrowheadPathMap[feature.fac_uid]!!.bounds.buffer(0.05)
        } else {
            selectOverlay = overlay
            bounds = LatLngBounds(overlay.position, overlay.position).buffer(0.05)
            arrowheadPath?.apply {
                outlineColor = WHITE
                outlineWidth = 2
                width = 5
            }
        }
        viewModel.viewModelScope.launch {
            try {
                val typ = feature.fac_typ
                val selectFeature = when (typ) {
                    "A" -> webService.featureGetA(fac_uid = feature.fac_uid).features.first()
                    "B" -> webService.featureGetB(fac_uid = feature.fac_uid).features.first()
                    "C" -> webService.featureGetC(fac_uid = feature.fac_uid).features.first()
                    "D" -> webService.featureGetD(fac_uid = feature.fac_uid).features.first()
                    "E" -> webService.featureGetE(fac_uid = feature.fac_uid).features.first()
                    "F" -> webService.featureGetF(fac_uid = feature.fac_uid).features.first()
                    else -> webService.featureGetA(fac_uid = feature.fac_uid).features.first()
                }
                viewModel.type(typ).set(selectFeature).overlay(selectOverlay).latLngBounds(bounds)
                naverMap.moveCamera(scrollTo(bounds.center).animate(Easing, 250).finishCallback {
                    val directions = NaverMapFragmentDirections.actionToFeatureFragment(type = typ)
                    findNavController().navigate(directions)
                })
            } catch (e: Exception) {
                snackbar(anchorView = fab, message = e.message).show()
                onFeatureGetResult(true, R.color.red_500)
            }
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
    private val poiCenterSet = mutableSetOf<Marker>()

    private lateinit var fab: FloatingActionButton
    private lateinit var bottomAppBar: BottomAppBar
    private lateinit var chipGroup: ChipGroup

    private lateinit var featureEdit: FeatureEdit
    private lateinit var districtTheme: DistrictTheme

    private lateinit var fabOnClickListener: OnClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        onCreateMap()
    }

    private fun onCreateMap() {
        val latLng = if (sharedPref.contains("latitude") && sharedPref.contains("longitude")) LatLng(
            sharedPref.getFloat("latitude", 0.0F).toDouble(), sharedPref.getFloat("longitude", 0.0F).toDouble()
        ) else LATLNG_GYEONGJU

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as MapFragment? ?: MapFragment.newInstance(
            NaverMapOptions().locale(Locale.KOREA).mapType(Satellite).contentPadding(0, 0, 0, naverMapPadding)
                .camera(CameraPosition(latLng, 16.0)).minZoom(10.0).extent(EXTENT_GYEONGJU).locationButtonEnabled(true)
                .tiltGesturesEnabled(false).msaaEnabled(true).symbolScale(1.5f)
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
        fabOnClickListener = OnClickListener {
            if (this.naverMap.cameraPosition.zoom < 13.0) {
                snackbar(fab, R.string.map_require_zoom).setAction("확대") {
                    this.naverMap.moveCamera(zoomTo(14.0).animate(Easing).finishCallback {
                        this.onFeatureGet()
                    })
                }.show()
            } else onFeatureGet()
        }
        fab.setOnClickListener(fabOnClickListener)
        bottomAppBar = requireActivity().findViewById(R.id.bottom_app_bar)
        chipGroup = requireActivity().findViewById(R.id.chipGroup)

        featureEdit = FeatureEdit(
            naverMap = naverMap,
            bottomAppBar = bottomAppBar,
            editViewModel = editViewModel,
            lifecycleOwner = viewLifecycleOwner,
        )

        districtTheme = DistrictTheme(
            naverMap = naverMap,
            executor = executor,
            handler = handler,
            resources = resources,
            chipGroup = chipGroup,
            viewModel = viewModel,
            lifecycleOwner = viewLifecycleOwner,
        )

        viewModel.throwable.observe(viewLifecycleOwner) {
            snackbar(anchorView = fab, message = "[에러] ${it.message}").show()
            onFeatureGetResult(true, R.color.red_500)
        }

        mobileUpdate()
    }

    private fun onCameraIdle() {
        sharedPref.edit().putFloat("latitude", naverMap.cameraPosition.target.latitude.toFloat())
            .putFloat("longitude", naverMap.cameraPosition.target.longitude.toFloat()).apply()
    }

    private fun onFeatureGet() {
        clearOverlays()
        onFeatureGetResult(false)
        viewModel.featuresGet(naverMap.contentBounds).observeOnce(viewLifecycleOwner) { featureList ->
            executor.execute {
                featureList.features.stream().forEach { feature ->
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
                    if (featureList.featureCount == 0) {
                        snackbar(fab, R.string.map_feature_get_empty).setAction("확인") {}.show()
                    }
                }
            }
        }
        viewModel.poiCenterGet(naverMap.contentBounds).observeOnce(viewLifecycleOwner) {
            executor.execute {
                it.pois.stream().forEach { poi ->
                    val latLngs = poi.geom.latLngs
                    latLngs.forEach { latLng ->
                        val color = toColor(poi)
                        poiCenterSet.add(createPOI(point = latLng[0], tintColor = color))
                    }
                }
                handler.post {
                    poiCenterSet.stream().forEach {
                        it.map = naverMap
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

    private fun createPOI(point: LatLng, @ColorInt tintColor: Int) =
        Marker(point, fromResource(R.drawable.ic_twotone_circle_18)).apply {
            anchor = PointF(0.5f, 0.5f)
            iconTintColor = tintColor
            isHideCollidedSymbols = true
            minZoom = 17.0
        }

    private fun createRegionMarker(point: LatLng, district: District) = Marker(point).apply {
        width = 1
        height = 1
        captionMinZoom = 12.0
        captionTextSize = 12.0f
        captionText = district.hjd_nam
        captionColor = WHITE
        captionHaloColor = BLACK
        subCaptionTextSize = 16.0f
        subCaptionText = district.bjd_nam
        subCaptionColor = WHITE
        subCaptionHaloColor = BLACK
        isHideCollidedSymbols = true
        tag = district.bjd_nam
        onClickListener = centerOnClickListener
    }

    private fun createArrowheadPath(line: List<LatLng>, @ColorInt tintColor: Int, feature: Feature) =
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
        arrowheadPath?.map = null
        markerMap.values.stream().forEach {
            if (it is Overlay) it.map = null
        }
        markerMap.clear()
        arrowheadPathMap.values.stream().forEach {
            if (it is Overlay) it.map = null
        }
        arrowheadPathMap.clear()
        poiCenterSet.stream().forEach {
            if (it is Overlay) it.map = null
        }
        poiCenterSet.clear()
    }

    private fun onRegionGet(menuItem: MenuItem) {
        menuItem.isChecked = !menuItem.isChecked
        if (menuItem.isChecked) {
            menuItem.setIcon(R.drawable.ic_round_toggle_on_24)
            viewModel.districtGet(naverMap.contentBounds).observeOnce(viewLifecycleOwner) {
                executor.execute {
                    it.districts.stream().forEach { region ->
                        val latLngs = region.geom.latLngs
                        latLngs.forEach { latLng ->
                            polygonMap[region.bjd_nam] = createPolygon(coords = latLng).also {
                                val centerLatlng = region.center.latLngs
                                centerMap[region.bjd_nam] = createRegionMarker(centerLatlng[0][0], region)
                            }
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
                snackbar(fab, R.string.map_region).setAction("확인") {}.show()
            }
        } else {
            menuItem.setIcon(R.drawable.ic_round_toggle_off_24)
            clearRegionOverlays()
        }
    }

    private fun createPolygon(coords: List<LatLng>) = PolygonOverlay(coords).apply {
        color = resources.getColor(android.R.color.transparent, null)
        outlineWidth = 5
        outlineColor = WHITE
    }

    private fun clearRegionOverlays() {
        centerMap.values.stream().forEach {
            if (it is Overlay) it.map = null
        }
        centerMap.clear()
        polygonMap.values.stream().forEach {
            if (it is Overlay) it.map = null
        }
        polygonMap.clear()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.bottomappbar_menu_fragment_map, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {/* R.id.action_legend -> {
                NavDrawerFragment().show(childFragmentManager, null)
                return true
            }*/

            R.id.action_district -> {
                onRegionGet(menuItem)
                return true
            }

            R.id.action_theme -> {
                districtTheme.toggle()
                return true
            }

            R.id.action_maptype -> {
                naverMap.mapType = if (naverMap.mapType == Satellite) Basic else Satellite
                return true
            }

            R.id.action_layergroup -> {
                naverMap.setLayerGroupEnabled(
                    LAYER_GROUP_CADASTRAL, !naverMap.isLayerGroupEnabled(LAYER_GROUP_CADASTRAL)
                )
                return true
            }

            R.id.action_search -> {
                val directions = NaverMapFragmentDirections.actionToPlaceSearchFragment(naverMap.cameraPosition.target)
                findNavController().navigate(directions)
                return true
            }

            R.id.action_undo -> featureEdit.undo()
            R.id.action_cancel -> featureEdit.cancel()

            R.id.action_a, R.id.action_b, R.id.action_c, R.id.action_d, R.id.action_e, R.id.action_f -> {
                onFeatureGetResult(false, R.color.yellow_A700, R.drawable.ic_round_pause_circle_24)
                featureEdit.pause()
                editViewModel.createFeature(id = menuItem.itemId).observeOnce(viewLifecycleOwner) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        onFeatureGet()
                        editViewModel.latLngs.clear(true)
                        featureEdit.cancel()
                        featureEdit.resume()
                        snackbar(fab, "신규 소규모 공공시설이 추가되었습니다").setAction("확인") {}.show()
                        onFeatureGetResult(true, R.color.teal_A400, R.drawable.ic_round_content_paste_search_30)
                    }, 3000)
                }
                return true
            }

            else -> return false
        }
    }

    private fun mobileUpdate() {
        viewModel.mobileUpdate(version = GIT_VERSION_NAME).observeOnce(viewLifecycleOwner) {
            when (it.status) {
                "OK" -> fab.setOnClickListener(fabOnClickListener)

                "FAIL" -> {
                    snackbar(anchorView = fab, message = "업데이트 있음: 최신 버전의 앱을 다운받아 설치해주세요").show()
                    toggleFab(true, R.color.yellow_A700, R.drawable.ic_round_system_update_24)
                    fab.setOnClickListener {
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://drive.google.com/file/d/1ljWYteI7sGIDHcq74sCM0K7KMJWJTk7n/view?usp=sharing")
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::naverMap.isInitialized) naverMap.setContentPadding(0, 0, 0, naverMapPadding)
        toggleFab(true)
    }

    override fun onPause() {
        chipGroup.visibility = GONE
        chipGroup.clearCheck()
        featureEdit.cancel()
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private class FeatureEdit(
        val naverMap: NaverMap,
        val bottomAppBar: BottomAppBar,
        val editViewModel: FeatureEditViewModel,
        lifecycleOwner: LifecycleOwner,
    ) {

        private val marker: Marker = Marker(MarkerIcons.BLACK).apply {
            iconTintColor = WHITE
            captionText = "시작점"
            captionColor = WHITE
            captionHaloColor = BLACK
        }
        private val path: ArrowheadPathOverlay = ArrowheadPathOverlay().apply {
            headSizeRatio = 3.5f
            width = 7
        }
        private val onMapLongClickListener = OnMapLongClickListener { _, coord ->
            val cameraUpdate = scrollTo(coord).animate(Linear).reason(REASON_GESTURE).finishCallback {
                when (editViewModel.size) {
                    0 -> {
                        bottomAppBar.replaceMenu(R.menu.bottomappbar_menu_fragment_map_edit)
                        marker.apply {
                            position = coord
                            map = naverMap
                        }
                        editViewModel.latLngs.add(coord)
                    }

                    1 -> {
                        editViewModel.latLngs.add(coord)
                        path.apply {
                            coords = editViewModel.coords
                            map = naverMap
                        }
                    }

                    else -> {
                        editViewModel.latLngs.add(coord)
                        path.coords = editViewModel.coords
                    }
                }
            }
            naverMap.moveCamera(cameraUpdate)
        }

        init {
            naverMap.onMapLongClickListener = onMapLongClickListener
            editViewModel.latLngs.observe(lifecycleOwner) {
                editViewModel.update()
                when (editViewModel.size) {
                    0 -> {
                        hideMenu(R.id.action_group_point)
                        hideMenu(R.id.action_group_line)
                        editViewModel
                    }

                    1 -> {
                        showMenu(R.id.action_group_point)
                        hideMenu(R.id.action_group_line)
                        editViewModel
                    }

                    else -> {
                        hideMenu(R.id.action_group_point)
                        showMenu(R.id.action_group_line)
                        editViewModel
                    }
                }
            }
        }

        fun undo(step: Int = 1): Boolean {
            when (editViewModel.size) {
                0, 1 -> {}
                2 -> {
                    path.map = null
                    editViewModel.latLngs.removeLast()
                }

                else -> {
                    path.coords = path.coords.dropLast(step)
                    editViewModel.latLngs.removeLast()
                }
            }
            return true
        }

        fun pause() {
            naverMap.onMapLongClickListener = null
        }

        fun resume() {
            naverMap.onMapLongClickListener = onMapLongClickListener
        }

        fun cancel(clearLatLng: Boolean = true): Boolean {
            marker.map = null
            path.apply {
                map = null
                coords.clear()
            }
            if (clearLatLng) editViewModel.latLngs.clear(true)
            bottomAppBar.replaceMenu(R.menu.bottomappbar_menu_fragment_map)
            return true
        }

        private fun showMenu(@IdRes id: Int) = bottomAppBar.menu.setGroupVisible(id, true)

        private fun hideMenu(@IdRes id: Int) = bottomAppBar.menu.setGroupVisible(id, false)
    }

    @Suppress("PrivatePropertyName")
    class DistrictTheme(
        naverMap: NaverMap,
        executor: ExecutorService,
        handler: Handler,
        resources: Resources,
        val chipGroup: ChipGroup,
        viewModel: FeatureViewModel,
        lifecycleOwner: LifecycleOwner,
    ) {

        private val white = resources.getColorStateList(R.color.white_1000, null)
        private val transparent = resources.getColorStateList(android.R.color.transparent, null)
        private val red_600 = resources.getColorStateList(R.color.red_600, null)
        private val deep_orange_500 = resources.getColorStateList(R.color.deep_orange_500, null)
        private val yellow_A400 = resources.getColorStateList(R.color.yellow_A400, null)
        private val green_600 = resources.getColorStateList(R.color.green_600, null)
        private val light_blue_A400 = resources.getColorStateList(R.color.light_blue_A400, null)
        private val light_green_A400 = resources.getColorStateList(R.color.light_green_A400, null)
        private val amber_A400 = resources.getColorStateList(R.color.amber_A400, null)
        private val purple_400 = resources.getColorStateList(R.color.purple_400, null)
        private val blue_A400 = resources.getColorStateList(R.color.blue_A400, null)

        private val red_600_25 = resources.getColorStateList(R.color.red_600_25, null)
        private val deep_orange_500_25 = resources.getColorStateList(R.color.deep_orange_500_25, null)
        private val yellow_A400_25 = resources.getColorStateList(R.color.yellow_A400_25, null)
        private val green_600_25 = resources.getColorStateList(R.color.green_600_25, null)
        private val light_blue_A400_25 = resources.getColorStateList(R.color.light_blue_A400_25, null)
        private val light_green_A400_25 = resources.getColorStateList(R.color.light_green_A400_25, null)
        private val amber_A400_25 = resources.getColorStateList(R.color.amber_A400_25, null)
        private val purple_400_25 = resources.getColorStateList(R.color.purple_400_25, null)
        private val blue_A400_25 = resources.getColorStateList(R.color.blue_A400_25, null)

        private val polygonMap = mutableMapOf<String, Set<PolygonOverlay>>()

        init {
            chipGroup.children.forEach { chip ->
                chip as Chip
                val chipColor = when (chip.id) {
                    R.id.chip1 -> purple_400
                    R.id.chip2 -> amber_A400
                    R.id.chip3 -> light_green_A400
                    R.id.chip4 -> light_blue_A400
                    R.id.chip5 -> green_600
                    R.id.chip6 -> yellow_A400
                    R.id.chip7 -> deep_orange_500
                    R.id.chip8 -> red_600
                    R.id.chip9 -> blue_A400
                    else -> white
                }
                val polygonColor = when (chip.id) {
                    R.id.chip1 -> purple_400_25
                    R.id.chip2 -> amber_A400_25
                    R.id.chip3 -> light_green_A400_25
                    R.id.chip4 -> light_blue_A400_25
                    R.id.chip5 -> green_600_25
                    R.id.chip6 -> yellow_A400_25
                    R.id.chip7 -> deep_orange_500_25
                    R.id.chip8 -> red_600_25
                    R.id.chip9 -> blue_A400_25
                    else -> transparent
                }
                chip.setOnCheckedChangeListener { _, isChecked ->
                    val tag: String = chip.tag as String
                    when (isChecked) {
                        true -> {
                            chip.chipBackgroundColor = chipColor
                            viewModel.themeGet(naverMap.contentBounds, tag).observeOnce(lifecycleOwner) {
                                executor.execute {
                                    val mutableSet = mutableSetOf<PolygonOverlay>()
                                    it.themes.stream().forEach { region ->
                                        val latLngs = region.geom.latLngs
                                        latLngs.stream().forEach { latLng ->
                                            mutableSet.add(PolygonOverlay(latLng).apply {
                                                color = polygonColor.defaultColor
                                                outlineColor = chipColor.defaultColor
                                                outlineWidth = 6
                                            })
                                        }
                                    }
                                    polygonMap[tag] = mutableSet
                                    handler.post {
                                        mutableSet.stream().forEach {
                                            it.map = naverMap
                                        }
                                    }
                                }
                            }
                        }

                        false -> {
                            chip.chipBackgroundColor = white
                            polygonMap[tag]?.stream()?.forEach {
                                it.map = null
                            }
                            polygonMap.remove(tag)
                        }
                    }
                }
            }
        }

        fun toggle() {
            with(chipGroup.visibility) {
                chipGroup.visibility = when (this) {
                    VISIBLE -> GONE
                    GONE -> VISIBLE
                    else -> GONE
                }
            }
        }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}
