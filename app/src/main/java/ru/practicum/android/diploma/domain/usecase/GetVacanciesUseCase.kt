package ru.practicum.android.diploma.domain.usecase

import ru.practicum.android.diploma.domain.models.VacancyResponse
import ru.practicum.android.diploma.domain.repository.VacanciesRepository
import ru.practicum.android.diploma.util.Resource

class GetVacanciesUseCase(
    private val repository: VacanciesRepository
) {
    suspend operator fun invoke(
        area: Int?,
        industry: Int?,
        text: String?,
        salary: Int?,
        page: Int,
        onlyWithSalary: Boolean
    ): Resource<VacancyResponse> {
        return repository.searchVacancies(
            area, industry, text, salary, page, onlyWithSalary
        ) as Resource<VacancyResponse>
    }
}
