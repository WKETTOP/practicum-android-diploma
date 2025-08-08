package ru.practicum.android.diploma.presentation.model

import ru.practicum.android.diploma.domain.models.VacancyResponse

sealed interface VacancySeatchUiState {

    data object Idle : VacancySeatchUiState

    data object Loading : VacancySeatchUiState

    data class Content(val data: VacancyResponse) : VacancySeatchUiState

    data class Error(val message: String?) : VacancySeatchUiState

    data object Emty : VacancySeatchUiState
}
