package ru.practicum.android.diploma.util

import java.text.NumberFormat

object NumberFormatter {

    fun formatSalary(amount: Int?): String {
        return amount?.let {
            NumberFormat.getInstance().format(it)
        } ?: ""
    }
}
