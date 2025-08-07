package ru.practicum.android.diploma.data.dto

data class VacancySearchRequest(
    val area: Int? = null,
    val industry: Int? = null,
    val text: String? = null,
    val salary: Int? = null,
    val page: Int = 1,
    val onlyWithSalary: Boolean = false
)
