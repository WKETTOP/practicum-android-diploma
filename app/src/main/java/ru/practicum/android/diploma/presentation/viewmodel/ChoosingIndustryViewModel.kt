package ru.practicum.android.diploma.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.interactor.FilterSettingsInteractor
import ru.practicum.android.diploma.domain.models.FilterIndustry
import ru.practicum.android.diploma.domain.models.Resource
import ru.practicum.android.diploma.domain.usecase.GetIndustriesUseCase

class ChoosingIndustryViewModel(
    private val getIndustriesUseCase: GetIndustriesUseCase,
    private val filterSettingsInteractor: FilterSettingsInteractor
) : ViewModel() {

    private val _industryState = MutableLiveData<Resource<List<FilterIndustry>>>()
    val industryState: LiveData<Resource<List<FilterIndustry>>> = _industryState

    private val _allIndustries = MutableStateFlow<List<FilterIndustry>>(emptyList())
    private val _filteredIndustries = MutableStateFlow<List<FilterIndustry>>(emptyList())
    val filteredIndustries: StateFlow<List<FilterIndustry>> = _filteredIndustries

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedIndustry = MutableStateFlow<FilterIndustry?>(null)
    val selectedIndustry: StateFlow<FilterIndustry?> = _selectedIndustry

    val isSelectButtonVisible: StateFlow<Boolean> =
        _selectedIndustry.map { it != null }.stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        viewModelScope.launch {
            val currentFilter = filterSettingsInteractor.getFilterParameters()
            _selectedIndustry.value = currentFilter.industry

            loadIndustries()
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        applyFilter()
    }

    fun selectIndustry(industry: FilterIndustry) {
        _selectedIndustry.value = industry
    }

    fun loadIndustries() {
        viewModelScope.launch {
            _industryState.value = Resource.Loading()
            _industryState.value = getIndustriesUseCase().also { resource ->
                if (resource is Resource.Success) {
                    _allIndustries.value = resource.data!!
                    applyFilter()
                }
            }
        }
    }

    private fun applyFilter() {
        val query = _searchQuery.value
        val allIndustries = _allIndustries.value

        if (allIndustries.isEmpty()) {
            return
        }

        _filteredIndustries.value = if (query.isBlank()) {
            allIndustries
        } else {
            allIndustries.filter { it.name.contains(query, ignoreCase = true) }
        }
    }
}
