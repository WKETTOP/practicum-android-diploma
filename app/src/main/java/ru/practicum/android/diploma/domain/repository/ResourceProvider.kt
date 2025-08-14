package ru.practicum.android.diploma.domain.repository

import androidx.annotation.StringRes

interface ResourceProvider {
    fun getString(@StringRes id: Int): String
}
