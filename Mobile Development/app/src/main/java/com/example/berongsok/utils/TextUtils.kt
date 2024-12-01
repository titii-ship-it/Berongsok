package com.example.berongsok.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.Locale

object TextUtils {
    fun formatRupiah(amount: Double): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        return numberFormat.format(amount).replace(",00", "")
    }

    fun formatWeight(weight: Double): String {
        return "%.2f kg".format(weight).replace(".00","")
    }

    fun formatPercentage(value: Double): String {
        val percentage = BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
        return String.format("%.3f%%", percentage)
    }
}