package com.example.berongsok.ui.historydetail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.berongsok.R
import com.example.berongsok.data.local.SettingPreferences
import com.example.berongsok.data.local.dataStore
import com.example.berongsok.databinding.ActivityHistoryDetailBinding
import com.example.berongsok.utils.TextUtils.formatDate
import com.example.berongsok.utils.TextUtils.formatRupiah
import com.example.berongsok.utils.TextUtils.formatWeight
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class HistoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryDetailBinding

    private val historyDetailViewModel: HistoryDetailViewModel by viewModels {
        HistoryDetailViewModelFactory()
    }

    companion object {
        const val EXTRA_HISTORY = "extra_history"
        const val EXTRA_TPS = "extra_TPS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val transactionId = intent.getStringExtra(EXTRA_HISTORY)
        val tpsId = intent.getStringExtra(EXTRA_TPS)

        Log.d("HistoryDetailActivity", "Received transactionId: $transactionId")
        Log.d("HistoryDetailActivity", "Received tpsId: $tpsId")


        if (transactionId.isNullOrEmpty()) {
            Log.e("HistoryDetailActivity", "Invalid transaction ID")
            return
        }

        if (tpsId.isNullOrEmpty()) {
            Log.e("HistoryDetailActivity", "Invalid tps ID")
        }
        Log.d("DetailHistoryActivity", "transaction ID: $transactionId")

        showLoading(true)
        lifecycleScope.launch {
            historyDetailViewModel.fetchHistoryDetailData(tpsId, transactionId)
        }

        historyDetailViewModel.historyDetailData.observe(this) { detailList ->
            showLoading(false)
            val detail = detailList?.find { it?.transactionId == transactionId }
            if (detail != null) {
                binding.tvNasabahName.text = detail.nasabahName ?: "Unknown Name"
                binding.tvWasteType.text = detail.wasteType ?: "Unknown Type"
                binding.tvTotalPrice.text = formatRupiah((detail.totalPrice ?: 0).toDouble())
                binding.tvCreateAt.text = detail.createAt?.let { formatDate(it) } ?: "Unknown date"
                binding.tvWeight.text =
                    detail.weight?.let { formatWeight(it.toDouble()) } ?: "Unknown weight"
                binding.tvTransactionId.text = detail.transactionId ?: "Unknown transaction id"
                binding.tvWastePrice.text = formatRupiah((detail.price ?: 0).toDouble())

                Glide.with(this)
                    .load(detail.imgUrl ?: R.drawable.ic_place_holder)
                    .into(binding.imgPredicted)
            } else {
                showError("No detail data found.")
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}