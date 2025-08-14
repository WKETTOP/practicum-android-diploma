package ru.practicum.android.diploma.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.domain.models.ErrorType
import ru.practicum.android.diploma.domain.models.Resource
import ru.practicum.android.diploma.domain.models.SearchParams
import ru.practicum.android.diploma.domain.models.VacancyDetail
import ru.practicum.android.diploma.domain.models.VacancyResponse
import ru.practicum.android.diploma.domain.repository.ResourceProvider
import ru.practicum.android.diploma.domain.usecase.GetVacanciesUseCase
import ru.practicum.android.diploma.presentation.model.VacancySearchUiState
import ru.practicum.android.diploma.util.debounce

class MainViewModel(
    private val resourceProvider: ResourceProvider,
    private val getVacanciesUseCase: GetVacanciesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<VacancySearchUiState>(VacancySearchUiState.Idle)
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

    private var cachedVacancyResponse: VacancyResponse? = null

    private val debouncedSearch = debounce<String>(
        delayMillis = SEARCH_DEBOUNCE,
        coroutineScope = viewModelScope,
        useLastParam = true
    ) { query ->
        performSearch(query)
    }

    fun onSearchQueryChanged(query: String) {
        if (query == lastQuery) return

        _searchQuery.value = query

        if (query.isBlank()) {
            currentJob?.cancel()
            _uiState.value = VacancySearchUiState.Idle
            cachedVacancyResponse = null
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

        setupInitialState(page)
        lastQuery = query

        currentJob = viewModelScope.launch {
            val params = createSearchParams(query, page)
            val result = getVacanciesUseCase(params)
            handleSearchResult(result, page)

            if (result is Resource.Success) {
                cachedVacancyResponse = createContentResponse(result.data!!)
            }
        }
    }

    private fun setupInitialState(page: Int) {
        if (page == 1) {
            vacanciesList.clear()
            _uiState.value = VacancySearchUiState.Loading
        } else {
            isLoadingNextPage = true
            _uiState.value = VacancySearchUiState.PaginationLoading(
                createVacancyResponse()
            )
        }
    }

    private fun createSearchParams(query: String, page: Int): SearchParams {
        return SearchParams(
            area = null,
            industry = null,
            text = query,
            salary = null,
            page = page,
            onlyWithSalary = false
        )
    }

    private fun handleSearchResult(result: Resource<VacancyResponse>, page: Int) {
        when (result) {
            is Resource.Success -> handleSuccess(result.data, page)
            is Resource.Error -> handleError(result.errorType ?: ErrorType.UNKNOWN, page)
            else -> Unit
        }
        isLoadingNextPage = false
        currentJob = null
    }

    private fun handleSuccess(data: VacancyResponse?, page: Int) {
        if (data == null) {
            _uiState.value = VacancySearchUiState.Error(errorType = ErrorType.EMPTY_RESPONSE)
            return
        }

        updatePaginationData(data, page)
        updateVacanciesList(data.vacancies)

        _uiState.value = if (vacanciesList.isNotEmpty()) {
            VacancySearchUiState.Content(createContentResponse(data))
        } else {
            VacancySearchUiState.Empty
        }
    }

    private fun updatePaginationData(data: VacancyResponse, page: Int) {
        if (page == 1) {
            totalFound = data.found
        }
        currentPage = data.page
        totalPages = data.pages
    }

    private fun updateVacanciesList(newVacancies: List<VacancyDetail>) {
        val uniqueVacancies = newVacancies.filterNot { v ->
            vacanciesList.any { it.id == v.id }
        }
        vacanciesList.addAll(uniqueVacancies)
    }

    private fun createContentResponse(data: VacancyResponse): VacancyResponse {
        return data.copy(
            found = totalFound,
            vacancies = vacanciesList
        )
    }

    private fun handleError(type: ErrorType, page: Int) {
        viewModelScope.launch { _toastMessage.emit(getErrorMessage(type)) }
        if (page > 1) {
            _uiState.value = VacancySearchUiState.Content(createVacancyResponse())
        } else {
            _uiState.value = VacancySearchUiState.Error(type)
        }
    }

    private fun createVacancyResponse(): VacancyResponse {
        return VacancyResponse(
            found = totalFound,
            pages = totalPages,
            page = currentPage,
            vacancies = vacanciesList
        )
    }

    private fun getErrorMessage(type: ErrorType): String {
        return when (type) {
            ErrorType.NO_INTERNET -> resourceProvider.getString(R.string.error_no_internet)
            ErrorType.SERVER_ERROR -> resourceProvider.getString(R.string.error_server)
            ErrorType.DATA_FORMAT_ERROR -> resourceProvider.getString(R.string.error_data_format)
            ErrorType.NOT_FOUND -> resourceProvider.getString(R.string.error_vacancy_not_found)
            ErrorType.EMPTY_RESPONSE -> resourceProvider.getString(R.string.error_response_empty)
            ErrorType.UNKNOWN -> resourceProvider.getString(R.string.error_unnown)
        }
    }

    fun loadNextPage() {
        val query = lastQuery ?: return
        if (!isLoadingNextPage && currentPage < totalPages && query.isNotBlank()) {
            performSearch(query, currentPage + 1)
        }
    }

    fun loadInitialDataIfNeeded() {
        if (cachedVacancyResponse != null) {
            _uiState.value = VacancySearchUiState.Content(cachedVacancyResponse!!)
        } else {
            _uiState.value = VacancySearchUiState.Idle
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE = 2000L
    }
}
