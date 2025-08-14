package ru.practicum.android.diploma.data.network

import ru.practicum.android.diploma.data.dto.Response

interface NetworkClient {
    suspend fun <T> doRequest(requestCall: suspend () -> retrofit2.Response<T>): Response<T>
}
