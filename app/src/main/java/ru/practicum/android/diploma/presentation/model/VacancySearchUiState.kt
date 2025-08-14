package ru.practicum.android.diploma.presentation.model

import ru.practicum.android.diploma.domain.models.ErrorType
import ru.practicum.android.diploma.domain.models.VacancyResponse

sealed interface VacancySearchUiState {

    data object Idle : VacancySearchUiState

    data object Loading : VacancySearchUiState

    data class Content(val data: VacancyResponse) : VacancySearchUiState

    data class Error(val errorType: ErrorType) : VacancySearchUiState

    data object Empty : VacancySearchUiState

    data class PaginationLoading(val currentData: VacancyResponse) : VacancySearchUiState
}
