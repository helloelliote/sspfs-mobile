/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.search

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.naver.maps.geometry.LatLng
import kr.djgis.sspfs.data.kakao.search.Document
import kr.djgis.sspfs.data.kakao.search.KeywordStore
import kr.djgis.sspfs.databinding.FragmentPlaceSearchBinding
import kr.djgis.sspfs.network.Moshi.moshiKeyword
import kr.djgis.sspfs.network.RetrofitClient.kakaoService
import kr.djgis.sspfs.network.enqueue
import kr.djgis.sspfs.util.getRecognizeSpeechIntent
import kr.djgis.sspfs.util.snackbar

class PlaceSearchFragment : Fragment(), KeywordAdapterListener {

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentPlaceSearchBinding? = null
    private val binding get() = _binding!!

    private val args: PlaceSearchFragmentArgs by navArgs()

    private val suggestionAdapter = SuggestionAdapter(this)
    private val historyAdapter = HistoryAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaceSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        setUpSearch(args.location)
        setUpSpeechToText()

        binding.recyclerViewSuggestion.adapter = suggestionAdapter
        binding.recyclerViewHistory.adapter = historyAdapter

        KeywordStore.DOCUMENT.observe(viewLifecycleOwner) { suggestionAdapter.submitList(it) }
        KeywordStore.HISTORY.observe(viewLifecycleOwner) { historyAdapter.submitList(it) }
    }

    private fun setUpSearch(latLng: LatLng?) {
        binding.searchQuery.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    kakaoService.searchKeyword(
                        query = query,
                        x = latLng?.longitude,
                        y = latLng?.latitude,
                    ).enqueue(onResponse = { json ->
                        val keyword = moshiKeyword.fromJson(json.toString())!!
                        if (keyword.meta.total_count == 0) {
                            resetSearch()
                        } else {
                            KeywordStore.DOCUMENT.postValue(keyword.documents.sortedBy { document ->
                                document.distance.toDouble()
                            }.toMutableList())
                        }
                    }, onFailure = {
                        snackbar(message = it).show()
                    })
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean = false
            })

            findViewById<View>(androidx.appcompat.R.id.search_close_btn).setOnClickListener {
                setQuery("", false)
                resetSearch()
            }
        }
    }

    private fun resetSearch() {
        KeywordStore.resetAll()?.let { itemCount: Int ->
            suggestionAdapter.notifyItemRangeRemoved(0, itemCount)
        }
    }

    private fun setUpSpeechToText() {
        val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val results = it.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                binding.searchQuery.setQuery(results?.get(0), true)
            }
        }

        binding.searchSpeechToText.setOnClickListener {
            activityResultLauncher.launch(getRecognizeSpeechIntent("찾으시는 장소를 말씀하세요"))
        }
    }

    override fun onKeywordItemClicked(cardView: View, document: Document) {
        if (!document.isClickable) {
            onDefaultKeywordItemClicked()
            return
        }
        KeywordStore.addHistory(document)
        val directions =
            PlaceSearchFragmentDirections.actionToNaverMapFragment(
                LatLng(
                    document.y.toDouble(),
                    document.x.toDouble()
                )
            )
        findNavController().navigate(directions)
    }

    private fun onDefaultKeywordItemClicked() {
        binding.searchQuery.run {
            this.requestFocus()
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
