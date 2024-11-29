package com.example.berongsok.ui.transaction

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.berongsok.MainActivity
import com.example.berongsok.R
import com.example.berongsok.data.local.SettingPreferences
import com.example.berongsok.data.local.dataStore
import com.example.berongsok.databinding.ActivityTransactionFormBinding
import com.example.berongsok.utils.Injection
import com.example.berongsok.utils.TextUtils

class TransactionFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTransactionFormBinding
    private val transactionViewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory(SettingPreferences.getInstance(application.dataStore), Injection.provideUserRepository())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        val wasteType = intent.getStringExtra(EXTRA_PREDICT_RESULT).toString()
        val wastePrice = intent.getStringExtra(EXTRA_PREDICT_PRICE).toString().toInt()
        val score = intent.getDoubleExtra(EXTRA_PREDICT_SCORE,0.0)
        val formattedScore = TextUtils.formatPercentage(score)

        binding.tvScore.text = formattedScore
        binding.tvWasteType.text = wasteType
        binding.edWasteType.setText(wasteType)
        binding.edWastePrice.setText(wastePrice.toString())

        Log.d("Predict Result", "Confidence Score: $score")

        binding.edWasteWeight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val weight = s.toString().toDoubleOrNull() ?: 0.0
                val totalPrice = weight * wastePrice
                val formattedPrice = TextUtils.formatRupiah(totalPrice)

                binding.tvTotalPrice.text = formattedPrice
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        imageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }

        binding.btnSubmit.setOnClickListener {
            val nasabahName = binding.edNasabahName.text.toString()
            val weight = binding.edWasteWeight.text.toString()
            val totalPrice = binding.tvTotalPrice.text.toString()

            if (imageUri != null) {
                showLoading(true)
                    transactionViewModel.addTransaction(imageUri, nasabahName, weight, totalPrice, wasteType,
                        wastePrice.toString(),this)
                transactionViewModel.addResult.observe(this) { result ->
                    result.onSuccess { response ->
                        if (response.status == "success") {
                            showLoading(false)
                            showRegistrationDialog(response.message)
                        } else {
                            showLoading(false)
                            showToast(response.message)
                        }
                    }
                    result.onFailure { throwable ->
                        throwable.localizedMessage?.let {
                            showErrorDialog(it)
                            showLoading(false)
                        }
                    }
                }
            } else {
                showToast("All fields are required")
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showRegistrationDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.succes_save)
            .setMessage(message)
            .setPositiveButton("Okay") { dialog, _ ->
                gotoMain()
            }
            .show()
    }

    private fun gotoMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Upload failed")
            .setMessage(message)
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_PREDICT_RESULT = "extra_predict_result"
        const val EXTRA_PREDICT_SCORE= "extra_predict_score"
        const val EXTRA_PREDICT_PRICE = "extra_predict_price"
    }
}