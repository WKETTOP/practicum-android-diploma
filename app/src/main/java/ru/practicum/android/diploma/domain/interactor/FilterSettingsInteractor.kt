package ru.practicum.android.diploma.domain.interactor

import ru.practicum.android.diploma.domain.models.FilterIndustry
import ru.practicum.android.diploma.domain.models.FilterParameters

interface FilterSettingsInteractor {

    fun getFilterParameters(): FilterParameters

    fun updateIndustry(industry: FilterIndustry?)

    fun updateOnlyWithSalary(onlyWithSalary: Boolean)

    fun clearAllFilters()

    fun hasActiveFilters(): Boolean
}
