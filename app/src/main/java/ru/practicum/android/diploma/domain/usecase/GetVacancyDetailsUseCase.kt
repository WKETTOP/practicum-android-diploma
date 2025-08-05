package ru.practicum.android.diploma.domain.usecase

import ru.practicum.android.diploma.domain.models.VacancyDetail
import ru.practicum.android.diploma.domain.repository.VacanciesRepository
import ru.practicum.android.diploma.util.Resource

class GetVacancyDetailsUseCase(
    private val repository: VacanciesRepository
) {
    suspend operator fun invoke(id: Int): Resource<VacancyDetail> {
        return repository.getVacancyDetails(id)
    }
}
