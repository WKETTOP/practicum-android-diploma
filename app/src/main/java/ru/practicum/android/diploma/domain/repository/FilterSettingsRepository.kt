package ru.practicum.android.diploma.domain.repository

import ru.practicum.android.diploma.domain.models.FilterParameters

interface FilterSettingsRepository {

    fun getFilterParameters(): FilterParameters

    fun saveFilterParameters(parameters: FilterParameters)

    fun clearFilterParameters()
}
