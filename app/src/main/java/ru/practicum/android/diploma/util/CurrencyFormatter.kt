package ru.practicum.android.diploma.util

import android.content.Context
import ru.practicum.android.diploma.R

object CurrencyFormatter {

    fun getCurrencySymbol(context: Context, currency: String): String {
        return when (currency) {
            "RUB", "RUR" -> context.getString(R.string.currency_rub)
            "USD" -> context.getString(R.string.currency_usd)
            "EUR" -> context.getString(R.string.currency_eur)
            "BYR" -> context.getString(R.string.currency_byr)
            "KZT" -> context.getString(R.string.currency_kzt)
            "UAH" -> context.getString(R.string.currency_uah)
            "AZN" -> context.getString(R.string.currency_azn)
            "UZS" -> context.getString(R.string.currency_uzs)
            "GEL" -> context.getString(R.string.currency_gel)
            "KGT" -> context.getString(R.string.currency_kgt)
            "HKD" -> context.getString(R.string.currency_hkd)
            "AUD" -> context.getString(R.string.currency_aud)
            "GBP" -> context.getString(R.string.currency_gbp)
            "SGD" -> context.getString(R.string.currency_sgd)
            else -> currency
        }
    }
}
