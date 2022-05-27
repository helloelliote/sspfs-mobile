/*
 * Copyright (c) 2022 대진기술정보(주) All Rights Reserved
 */

package kr.djgis.sspfs.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.djgis.sspfs.data.kakao.search.Document
import kr.djgis.sspfs.data.kakao.search.DocumentDiffCallback
import kr.djgis.sspfs.databinding.KeywordItemLayoutBinding

class SuggestionAdapter(
    private val listener: KeywordAdapterListener,
) : ListAdapter<Document, SuggestionAdapter.SuggestionViewHolder>(DocumentDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        return SuggestionViewHolder(
            KeywordItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SuggestionViewHolder(
        private val binding: KeywordItemLayoutBinding,
        listener: KeywordAdapterListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.run {
                this.listener = listener
            }
        }

        fun bind(document: Document) {
            binding.run {
                this.document = document
                executePendingBindings()
            }
        }
    }
}
