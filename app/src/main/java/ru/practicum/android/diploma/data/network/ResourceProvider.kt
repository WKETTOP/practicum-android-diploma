package ru.practicum.android.diploma.data.network

interface ResourceProvider {
    fun getString(id: Int): String
}
