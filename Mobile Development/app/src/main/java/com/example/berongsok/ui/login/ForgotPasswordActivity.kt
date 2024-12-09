package com.example.berongsok.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.berongsok.R
import com.example.berongsok.databinding.ActivityForgotPasswordBinding
import com.example.berongsok.utils.Injection

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(Injection.provideUserRepository())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        showLoading(false)

        binding.btnSendOTP.setOnClickListener {
            val email = binding.edVerifyEmail.getEmail()

            if (email.isNotBlank()) {
                loginViewModel.sendOTP(email)
            } else {
                Toast.makeText(this, "Email required", Toast.LENGTH_SHORT).show()
            }
        }

        loginViewModel.loading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        binding.btnVerify.setOnClickListener {
            val email = binding.edVerifyEmail.getEmail()
            val otp = binding.edVerifyOtp.getOTP()
            val newPassword = binding.edNewPassword.getPassword()
            val confirmPassword = binding.edConfirmPassword.text.toString()

            if (email.isNotBlank() && newPassword.isNotBlank() && confirmPassword.isNotBlank() && otp.isNotBlank()) {
                if (newPassword == confirmPassword) {
                    loginViewModel.resetPassword(email, otp.toInt(), newPassword)
                } else {
                    Toast.makeText(this, R.string.password_mismatch, Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, R.string.fields_required, Toast.LENGTH_SHORT).show()
            }
        }

        loginViewModel.otpResult.observe(this) { result ->
            result.onSuccess { response ->
                if (response.status == "success") {
                    showOTPDialog(response.message)
                    binding.edNewPassword.visibility = VISIBLE
                    binding.confirmLayout.visibility = VISIBLE
                } else {
                    showOTPDialog(response.message)
                }
            }
            result.onFailure { throwable ->
                throwable.localizedMessage?.let {
                    showToast(it)
                    showLoading(false)
                }
            }
        }

        loginViewModel.resetResult.observe(this) { result ->
            result.onSuccess { response ->
                if (response.status == "success") {
                    showResetDialog(response.message)
                } else {
                    showOTPDialog(response.message)
                }
            }
            result.onFailure { throwable ->
                throwable.localizedMessage?.let {
                    showToast(it)
                    showLoading(false)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) VISIBLE else GONE
    }

    private fun showOTPDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.otp_status)
            .setMessage(message)
            .setPositiveButton(R.string.Okay) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showResetDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.succes_reset)
            .setMessage(message)
            .setPositiveButton(R.string.continue_login) { dialog, _ ->
                goToLogin()
            }
            .show()
    }

    private fun goToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}