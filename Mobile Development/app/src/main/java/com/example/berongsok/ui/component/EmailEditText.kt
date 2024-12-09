package com.example.berongsok.ui.component

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import android.widget.LinearLayout
import com.example.berongsok.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EmailEditTex @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val inputLayout: TextInputLayout
    private val editText: TextInputEditText

    init {
        LinearLayout.inflate(context, R.layout.edit_text_email, this)
        inputLayout = findViewById(R.id.email_layout)
        editText = findViewById(R.id.et_email)
        setupTextWatcher()
    }

    private fun setupTextWatcher() {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    inputLayout.error = context.getString(R.string.email_invalid)
                } else {
                    inputLayout.helperText = context.getString(R.string.email_valid)
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    fun getEmail(): String {
        val email = editText.text.toString()
        return email
    }
}