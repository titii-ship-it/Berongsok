package com.example.berongsok.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.berongsok.R
import com.example.berongsok.databinding.ActivityVerifyOtpactivityBinding
import com.example.berongsok.ui.login.LoginActivity
import com.example.berongsok.utils.Injection

class VerifyOTPActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifyOtpactivityBinding
    private val registerViewModel: RegisterViewModel by viewModels {
        RegisterViewModelFactory(Injection.provideUserRepository())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVerifyOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading(false)

        registerViewModel.loading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        val email = intent.getStringExtra(EXTRA_EMAIL)

        binding.btnVerify.setOnClickListener {
            val otpCode = binding.edVerifyOtp.getOTP()

            if (otpCode.isNotBlank()) {
                if (email != null) {
                    registerViewModel.verify(email, otpCode.toInt())
                }
            } else {
                Toast.makeText(this, R.string.fields_required, Toast.LENGTH_SHORT).show()
            }
        }

        registerViewModel.registerResult.observe(this) { result ->
            result.onSuccess { response ->
                if (!response.error) {
                    showRegistrationDialog(response.message)
                } else {
                    showErrorDialog(response.message)
                }
            }
            result.onFailure { throwable ->
                throwable.localizedMessage?.let {
                    showErrorDialog(it)
                    showLoading(false)
                }
            }
        }
    }

    private fun showRegistrationDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.succes_regist)
            .setMessage(message)
            .setPositiveButton(R.string.continue_login) { _, _ ->
                goToLogin()
            }
            .show()
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.failed_regist)
            .setMessage(message)
            .setPositiveButton(R.string.retry) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    companion object {
        const val EXTRA_EMAIL = "extra_email"
    }
}