package ru.practicum.android.diploma.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.models.SearchParams
import ru.practicum.android.diploma.domain.usecase.GetVacanciesUseCase
import ru.practicum.android.diploma.presentation.model.VacancySeatchUiState
import ru.practicum.android.diploma.util.Resource

class MainViewModel(
    private val getVacanciesUseCase: GetVacanciesUseCase
) : ViewModel() {

    private var _uiState = MutableStateFlow<VacancySeatchUiState>(VacancySeatchUiState.Idle)
    val uiState: StateFlow<VacancySeatchUiState> = _uiState

    private var _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private var currentJob: Job? = null

    private val debouncedSearch = ru.practicum.android.diploma.util.debounce<String>(
        delayMillis = SEARCH_DEBOUNCE,
        coroutineScope = viewModelScope,
        useLastParam = true
    ) { query ->
        performSearch(query)
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query

        if (query.isBlank()) {
            currentJob?.cancel()
            _uiState.value = VacancySeatchUiState.Idle
        } else {
            debouncedSearch(query)
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        currentJob?.cancel()
        _uiState.value = VacancySeatchUiState.Idle
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            return
        }

        currentJob?.cancel()
        _uiState.value = VacancySeatchUiState.Loading

        currentJob = viewModelScope.launch {
            val params = SearchParams(
                area = null,
                industry = null,
                text = query,
                salary = null,
                page = 1,
                onlyWithSalary = false
            )
            when (val res = getVacanciesUseCase(params)) {
                is Resource.Success -> {
                    val data = res.data
                    if (data != null) {
                        _uiState.value = if (data.vacancies.isNotEmpty()) {
                            VacancySeatchUiState.Content(data)
                        } else {
                            VacancySeatchUiState.Emty
                        }
                    } else {
                        _uiState.value = VacancySeatchUiState.Error(null)
                    }
                }

                is Resource.Error -> {
                    _uiState.value = VacancySeatchUiState.Error(res.message)
                }

                is Resource.Loading -> {
                    _uiState.value = VacancySeatchUiState.Loading
                }
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE = 2000L
    }
}
