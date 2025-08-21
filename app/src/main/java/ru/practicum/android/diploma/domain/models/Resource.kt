package ru.practicum.android.diploma.domain.models

sealed class Resource<T>(
    val data: T? = null,
    val errorType: ErrorType? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(errorType: ErrorType, data: T? = null) : Resource<T>(data, errorType = errorType)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}
