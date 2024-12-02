package com.example.berongsok.ui.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.berongsok.data.local.SettingPreferences
import com.example.berongsok.data.local.dataStore
import com.example.berongsok.databinding.FragmentHistoryBinding
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val historyViewModel: HistoryViewModel by viewModels {
        HistoryViewModelFactory(SettingPreferences.getInstance(requireActivity().applicationContext.dataStore))
    }
    private lateinit var historyAdapter: HistoryAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        historyAdapter = HistoryAdapter()
        binding.rvHistoryTransaction.layoutManager = LinearLayoutManager(requireActivity())
        binding.rvHistoryTransaction.adapter = historyAdapter
        Log.d("HistoryFragment", "RecyclerView adapter dan layout manager sudah diatur")

        historyViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        historyViewModel.historyData.observe(viewLifecycleOwner) { listHistory ->
            if (listHistory != null) {
                Log.d("HistoryFragment", "Data untuk RecyclerView: $listHistory")
                val sortedList = listHistory.sortedByDescending { it?.createAt }
                historyAdapter.submitList(sortedList)
            } else {
                binding.noTransaction.visibility = VISIBLE
                Log.d("HistoryFragment", "Data kosong untuk RecyclerView")
                historyAdapter.submitList(emptyList())

            }
        }

        val pref = SettingPreferences.getInstance(requireActivity().applicationContext.dataStore)
        lifecycleScope.launch {
            pref.tpsId.collect() { tpsId ->
                tpsId?.let {
                    historyViewModel.fetchHistoryData(it)
                }
            }
        }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}