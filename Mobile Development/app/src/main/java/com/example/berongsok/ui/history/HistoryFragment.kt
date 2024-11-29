package com.example.berongsok.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.berongsok.data.local.SettingPreferences
import com.example.berongsok.data.local.dataStore
import com.example.berongsok.databinding.FragmentHistoryBinding
import com.example.berongsok.ui.transaction.TransactionViewModel
import com.example.berongsok.ui.transaction.TransactionViewModelFactory
import com.example.berongsok.utils.Injection

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
//        val historyViewModel =
//            ViewModelProvider(this).get(HistoryViewModel::class.java)

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.tvHistory.visibility = VISIBLE

        historyViewModel.getStories()

        historyAdapter = HistoryAdapter()
        binding.rvHistoryTransaction.adapter = historyAdapter
        binding.rvHistoryTransaction.layoutManager = LinearLayoutManager(requireActivity())

        val textView: TextView = binding.textDashboard
        historyViewModel.result.observe(viewLifecycleOwner) { listHistory ->
            if (listHistory != null) {
                listHistory?.let {
                    historyAdapter.submitList(listHistory)



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