package com.ravi.skinhealthyai.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.ravi.skinhealthyai.data.model.History
import com.ravi.skinhealthyai.databinding.ItemHistoryBinding
import com.ravi.skinhealthyai.utils.formatDate

class LastHistoryAdapter(private val onItemClickCallback: OnItemClickCallback) :
    ListAdapter<History, LastHistoryAdapter.WordViewHolder>(WordsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordViewHolder(binding, onItemClickCallback)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val history = getItem(position) as History
        holder.bind(history)
    }

    class WordViewHolder(private val binding: ItemHistoryBinding,  private val onItemClickCallback: OnItemClickCallback) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: History) {
            binding.titleDisease.text = data.nameSkinDisease
            binding.dateDisease.text = formatDate(data.createdAt)
            Glide.with(binding.root.context)
                .load(data.photo)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(12)))
                .into(binding.imgCard)
            binding.root.setOnClickListener {
                onItemClickCallback.onItemClicked(data)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: History)
    }

    class WordsComparator : DiffUtil.ItemCallback<History>() {
        override fun areItemsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: History, newItem: History): Boolean {
            return oldItem.id == newItem.id
        }
    }
}
