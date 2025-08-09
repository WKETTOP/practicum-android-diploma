package ru.practicum.android.diploma.data.dto

data class Response<T>(
    val resultCode: Int,
    val data: T? = null
)
