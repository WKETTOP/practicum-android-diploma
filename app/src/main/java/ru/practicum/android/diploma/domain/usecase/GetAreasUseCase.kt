package ru.practicum.android.diploma.domain.usecase

import ru.practicum.android.diploma.domain.models.FilterArea
import ru.practicum.android.diploma.domain.repository.VacanciesRepository
import ru.practicum.android.diploma.domain.models.Resource

class GetAreasUseCase(
    private val repository: VacanciesRepository
) {
    suspend operator fun invoke(): Resource<List<FilterArea>> {
        return repository.getAreas()
    }
}
