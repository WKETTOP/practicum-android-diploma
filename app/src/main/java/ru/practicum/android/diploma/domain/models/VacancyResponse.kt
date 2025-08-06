package ru.practicum.android.diploma.domain.models

data class VacancyResponse(
    val found: Int,
    val pages: Int,
    val page: Int,
    val vacancies: List<VacancyDetail>
)
