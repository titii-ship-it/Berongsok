package com.example.berongsok.ui.component

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.LinearLayout
import com.example.berongsok.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class OTPEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val inputLayout: TextInputLayout
    private val editText: TextInputEditText

    init {
        inflate(context, R.layout.edit_text_otp, this)
        inputLayout = findViewById(R.id.otp_layout)
        editText = findViewById(R.id.et_otp)
        setupTextWatcher()
    }

    private fun setupTextWatcher() {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length != 6) {
                    inputLayout.error = context.getString(R.string.invalid_otp)
                } else {
                    inputLayout.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    fun getOTP(): String {
        val otp = editText.text.toString()
        return otp
    }
}