package ru.practicum.android.diploma.domain.usecase

import ru.practicum.android.diploma.domain.models.FilterIndustry
import ru.practicum.android.diploma.domain.repository.VacanciesRepository
import ru.practicum.android.diploma.domain.models.Resource

class GetIndustriesUseCase(
    private val repository: VacanciesRepository
) {
    suspend operator fun invoke(query: String? = null): Resource<List<FilterIndustry>> {
        return repository.getIndustries(query)
    }
}
