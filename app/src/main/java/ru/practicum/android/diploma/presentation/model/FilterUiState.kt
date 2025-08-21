package ru.practicum.android.diploma.presentation.model

import ru.practicum.android.diploma.domain.models.FilterIndustry

data class FilterUiState(
    val selectedIndustry: FilterIndustry? = null,
    val onlyWithSalary: Boolean = false,
) {

    sealed class NavigationEvent {
        object NavigationBack : NavigationEvent()
    }
}
