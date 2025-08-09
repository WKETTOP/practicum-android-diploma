package ru.practicum.android.diploma.domain.usecase

import ru.practicum.android.diploma.domain.models.SearchParams
import ru.practicum.android.diploma.domain.models.VacancyResponse
import ru.practicum.android.diploma.domain.repository.VacanciesRepository
import ru.practicum.android.diploma.domain.models.Resource

class GetVacanciesUseCase(
    private val repository: VacanciesRepository
) {
    suspend operator fun invoke(params: SearchParams): Resource<VacancyResponse> {
        return repository.searchVacancies(params)
    }
}
