package ru.practicum.android.diploma.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.models.Resource
import ru.practicum.android.diploma.domain.models.SearchParams
import ru.practicum.android.diploma.domain.models.VacancyDetail
import ru.practicum.android.diploma.domain.models.VacancyResponse
import ru.practicum.android.diploma.domain.usecase.GetVacanciesUseCase
import ru.practicum.android.diploma.presentation.model.VacancySearchUiState
import ru.practicum.android.diploma.util.debounce

class MainViewModel(
    private val getVacanciesUseCase: GetVacanciesUseCase
) : ViewModel() {

    private var _uiState = MutableStateFlow<VacancySearchUiState>(VacancySearchUiState.Idle)
    val uiState: StateFlow<VacancySearchUiState> = _uiState

    private var _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _toastMessage = MutableSharedFlow<String?>(replay = 1)
    val toastMessage: SharedFlow<String?> = _toastMessage

    private var currentJob: Job? = null

    private var currentPage = 1
    private var totalPages = 1
    private var isLoadingNextPage = false
    private var lastQuery: String? = null
    private val vacanciesList = mutableListOf<VacancyDetail>()
    private var totalFound = 0

    private val debouncedSearch = debounce<String>(
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
            _uiState.value = VacancySearchUiState.Idle
        } else {
            debouncedSearch(query)
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        currentJob?.cancel()
        _uiState.value = VacancySearchUiState.Idle
    }

    private fun performSearch(query: String, page: Int = 1) {
        if (query.isBlank()) return

        currentJob?.cancel()

        if (page == 1) {
            vacanciesList.clear()
            _uiState.value = VacancySearchUiState.Loading
        } else {
            isLoadingNextPage = true
            _uiState.value = VacancySearchUiState.PaginationLoading(
                VacancyResponse(
                    found = totalFound,
                    pages = totalPages,
                    page = currentPage,
                    vacancies = vacanciesList
                )
            )
        }

        lastQuery = query
        currentJob = viewModelScope.launch {
            val params = SearchParams(
                area = null,
                industry = null,
                text = query,
                salary = null,
                page = page,
                onlyWithSalary = false
            )
            when (val res = getVacanciesUseCase(params)) {
                is Resource.Success -> {
                    val data = res.data
                    if (data != null) {
                        if (page == 1) {
                            totalFound = data.found
                        }
                        currentPage = data.page
                        totalPages = data.pages

                        val newVacancies = data.vacancies.filterNot { v ->
                            vacanciesList.any { it.id == v.id }
                        }
                        vacanciesList.addAll(newVacancies)

                        _uiState.value = if (vacanciesList.isNotEmpty()) {
                            VacancySearchUiState.Content(
                                data.copy(
                                    found = totalFound,
                                    vacancies = vacanciesList
                                )
                            )
                        } else {
                            VacancySearchUiState.Empty
                        }
                    } else {
                        _uiState.value = VacancySearchUiState.Error("Ошибка сервера")
                    }
                }
                is Resource.Error -> {
                    if (page > 1) {
                        _uiState.value = VacancySearchUiState.Content(
                            VacancyResponse(
                                found = totalFound,
                                pages = totalPages,
                                page = currentPage,
                                vacancies = vacanciesList
                            )
                        )
                        _toastMessage.tryEmit(res.message)
                    } else {
                        _uiState.value = VacancySearchUiState.Error(res.message)
                    }
                }
                else -> Unit
            }
            isLoadingNextPage = false
        }
    }

    fun loadNextPage() {
        if (!isLoadingNextPage && currentPage < totalPages && !lastQuery.isNullOrBlank()) {
            performSearch(lastQuery!!, currentPage + 1)
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE = 2000L
    }
}
