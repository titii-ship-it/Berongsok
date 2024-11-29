package com.example.berongsok.ui.history

import android.content.Intent
import android.provider.ContactsContract.Data
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.berongsok.data.remote.response.DataItem
import com.example.berongsok.databinding.ItemHistoryBinding

class HistoryAdapter: ListAdapter<DataItem, HistoryAdapter.HistoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryAdapter.HistoryViewHolder {
//        TODO("Not yet implemented")
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    class HistoryViewHolder(private val binding: ItemHistoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(history: DataItem) {

            val history = history.history
            if (history != null) {
                binding.tvItemNasabah.text = history.nasabahName
                binding.tvItemWasteType.text = history.wasteType
                binding.tvItemTotalPrice.text = history.totalPrice.toString()

                Glide.with(itemView.context)
                    .load(history.imgUrl)
                    .into(binding.imgPredicted)
//
//                itemView.setOnClickListener {
//                    val intent = Intent(itemView.context, DetailHistoryActivity::classjava)
//                }


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