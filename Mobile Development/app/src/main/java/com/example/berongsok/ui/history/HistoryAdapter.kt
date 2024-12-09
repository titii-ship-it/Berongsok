package com.example.berongsok.ui.history

import android.content.ContentValues
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.berongsok.R
import com.example.berongsok.data.remote.response.DataItem
import com.example.berongsok.databinding.ItemHistoryBinding
import com.example.berongsok.ui.historydetail.HistoryDetailActivity
import com.example.berongsok.ui.historydetail.HistoryDetailActivity.Companion.EXTRA_HISTORY
import com.example.berongsok.ui.historydetail.HistoryDetailActivity.Companion.EXTRA_TPS
import com.example.berongsok.utils.TextUtils.formatDate
import com.example.berongsok.utils.TextUtils.formatRupiah

class HistoryAdapter : ListAdapter<DataItem, HistoryAdapter.HistoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryAdapter.HistoryViewHolder {
//        TODO("Not yet implemented")
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(history: DataItem) {
            Log.d("HistoryAdapter", "Data binding: $history")

            binding.tvItemNasabah.text = history.nasabahName ?: "unknown name"
            binding.tvItemCreateAt.text = history.createAt?.let { formatDate(it) } ?: "unknown date"
            binding.tvItemWasteType.text = history.wasteType ?: "Unknown type"
            binding.tvItemTotalPrice.text = formatRupiah((history.totalPrice ?: 0).toDouble())


            val imgUrl = history.imgUrl
            Log.d("HistoryAdapter", "Image URL: $imgUrl") // Add this log to see the actual URL

            if (imgUrl.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(R.drawable.ic_place_holder)
                    .into(binding.imgPredicted)
            } else {
                Glide.with(itemView.context)
                    .load(imgUrl)
                    .into(binding.imgPredicted)
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, HistoryDetailActivity::class.java)
                intent.putExtra(EXTRA_HISTORY, history.transactionId)
                intent.putExtra(EXTRA_TPS, history.tpsId)
                Log.d(ContentValues.TAG, "trasactiondetail: ${history.transactionId} ")
                Log.d(ContentValues.TAG, "tps id detail: ${history.tpsId} ")

                itemView.context.startActivity(intent)
            }
        }
    }


    override fun onBindViewHolder(holder: HistoryAdapter.HistoryViewHolder, position: Int) {
//        TODO("Not yet implemented")
        val history = getItem(position)
        holder.bind(history)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItem>() {
            override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: DataItem,
                newItem: DataItem
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}
