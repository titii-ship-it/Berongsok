package com.example.berongsok.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.berongsok.data.local.SettingPreferences
import com.example.berongsok.data.local.dataStore
import com.example.berongsok.databinding.FragmentDashboardBinding
import com.example.berongsok.ui.history.HistoryViewModel
import com.example.berongsok.ui.history.HistoryViewModelFactory
import com.example.berongsok.ui.profile.ProfileActivity
import com.example.berongsok.ui.transaction.TransactionViewModel
import com.example.berongsok.ui.transaction.TransactionViewModelFactory
import com.example.berongsok.utils.Injection
import com.example.berongsok.utils.TextUtils.formatRupiah
import com.example.berongsok.utils.TextUtils.formatWeight

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    private val dashboardViewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(
            SettingPreferences.getInstance(requireActivity().dataStore),
            Injection.provideUserRepository()
        )
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val dashboardViewModel =
//            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard

        dashboardViewModel.getDashboardData()
        dashboardViewModel.dashboardResult.observe(viewLifecycleOwner) {result ->
            result.onSuccess { response ->
                binding.tvTotalBalance.text = formatRupiah((response.totalPrice ?: 0).toDouble())
//                formatRupiah((history.totalPrice ?: 0).toDouble())

                val plasticCupWeight = response.data?.find { it?.wasteType == "plastic-cup" }?. totalWeight ?: 0
                binding.tvPlasticCup.text = formatWeight(plasticCupWeight.toDouble())

                val plasticBottleWeight = response.data?.find { it?.wasteType == "plastic-bottle" }?. totalWeight ?: 0
                binding.tvPlasticBottle.text = formatWeight(plasticBottleWeight.toDouble())

                val canWeight = response.data?.find { it?.wasteType == "can" }?. totalWeight ?: 0
                binding.tvCan.text = formatWeight(canWeight.toDouble())

                val glassBottleWeight = response.data?.find { it?.wasteType == "glass-bottle" }?. totalWeight ?: 0
                binding.tvGlassBottle.text = formatWeight(glassBottleWeight.toDouble())

                val cardBoardWeight = response.data?.find { it?.wasteType == "cardboard" }?. totalWeight ?: 0
                binding.tvCardboard.text = formatWeight(cardBoardWeight.toDouble())

                val paperWeight = response.data?.find { it?.wasteType == "paper" }?. totalWeight ?: 0
                binding.tvPaper.text = formatWeight(paperWeight.toDouble())

            }
        }

        binding.profileBtn.setOnClickListener { gotoProfile() }

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun gotoProfile(){
        startActivity(Intent(activity, ProfileActivity::class.java))
    }
}