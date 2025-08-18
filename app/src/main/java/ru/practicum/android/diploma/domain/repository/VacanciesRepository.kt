package ru.practicum.android.diploma.domain.repository

import ru.practicum.android.diploma.domain.models.FilterArea
import ru.practicum.android.diploma.domain.models.FilterIndustry
import ru.practicum.android.diploma.domain.models.SearchParams
import ru.practicum.android.diploma.domain.models.VacancyDetail
import ru.practicum.android.diploma.domain.models.VacancyResponse
import ru.practicum.android.diploma.domain.models.Resource

interface VacanciesRepository {
    suspend fun searchVacancies(
        params: SearchParams
    ): Resource<VacancyResponse>

    suspend fun getVacancyDetails(id: String): Resource<VacancyDetail>

    suspend fun getAreas(): Resource<List<FilterArea>>

    suspend fun getIndustries(query: String? = null): Resource<List<FilterIndustry>>
}
