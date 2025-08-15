package ru.practicum.android.diploma.data.repository

import ru.practicum.android.diploma.data.storage.FilterStorage
import ru.practicum.android.diploma.domain.models.FilterParameters
import ru.practicum.android.diploma.domain.repository.FilterSettingsRepository

class FilterSettingsRepositoryImpl(
    private val filterStorage: FilterStorage
) : FilterSettingsRepository {

    override fun getFilterParameters(): FilterParameters {
        return filterStorage.getFilterParameters()
    }

    override fun saveFilterParameters(parameters: FilterParameters) {
        filterStorage.saveFilterParameters(parameters)
    }

    override fun clearFilterParameters() {
        filterStorage.clearFilterParameters()
    }
}
