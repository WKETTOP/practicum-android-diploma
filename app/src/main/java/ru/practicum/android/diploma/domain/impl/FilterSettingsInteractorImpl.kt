package ru.practicum.android.diploma.domain.impl

import ru.practicum.android.diploma.domain.interactor.FilterSettingsInteractor
import ru.practicum.android.diploma.domain.models.FilterIndustry
import ru.practicum.android.diploma.domain.models.FilterParameters
import ru.practicum.android.diploma.domain.models.hasActiveFilters
import ru.practicum.android.diploma.domain.repository.FilterSettingsRepository

class FilterSettingsInteractorImpl(
    private val repository: FilterSettingsRepository
) : FilterSettingsInteractor {

    override fun getFilterParameters(): FilterParameters {
        return repository.getFilterParameters()
    }

    override fun updateIndustry(industry: FilterIndustry?) {
        val currentParams = repository.getFilterParameters()
        val updateParams = currentParams.copy(industry = industry)
        repository.saveFilterParameters(updateParams)
    }

    override fun updateOnlyWithSalary(onlyWithSalary: Boolean) {
        val currentParams = repository.getFilterParameters()
        val updateParams = currentParams.copy(onlyWithSalary = onlyWithSalary)
        repository.saveFilterParameters(updateParams)
    }

    override fun clearAllFilters() {
        repository.clearFilterParameters()
    }

    override fun hasActiveFilters(): Boolean {
        return repository.getFilterParameters().hasActiveFilters()
    }
}
