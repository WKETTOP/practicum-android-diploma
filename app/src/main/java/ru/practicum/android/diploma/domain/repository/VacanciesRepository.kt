package ru.practicum.android.diploma.domain.repository

import ru.practicum.android.diploma.domain.models.FilterArea
import ru.practicum.android.diploma.domain.models.FilterIndustry
import ru.practicum.android.diploma.domain.models.VacancyDetail
import ru.practicum.android.diploma.domain.models.VacancyResponse
import ru.practicum.android.diploma.util.Resource

interface VacanciesRepository {
    suspend fun searchVacancies(
        area: Int?,
        industry: Int?,
        text: String?,
        salary: Int?,
        page: Int,
        onlyWithSalary: Boolean
    ): Resource<VacancyResponse>

    suspend fun getVacancyDetails(id: Int): Resource<VacancyDetail>

    suspend fun getAreas(): Resource<List<FilterArea>>

    suspend fun getIndustries(): Resource<List<FilterIndustry>>
}
