package ru.practicum.android.diploma.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.interactor.FilterSettingsInteractor
import ru.practicum.android.diploma.domain.models.FilterIndustry
import ru.practicum.android.diploma.presentation.model.FilterUiState

class FilterSettingsViewModel(
    private val filterSettingsInteractor: FilterSettingsInteractor
) : ViewModel() {

    private val _uiState = MutableStateFlow(FilterUiState())
    val uiState: StateFlow<FilterUiState> = _uiState

    private val _navigationEvent = MutableStateFlow<FilterUiState.NavigationEvent?>(null)
    val navigationEvent: StateFlow<FilterUiState.NavigationEvent?> = _navigationEvent

    init {
        loadFilterSettings()
    }

    private fun loadFilterSettings() {
        val filterParameters = filterSettingsInteractor.getFilterParameters()
        _uiState.value = FilterUiState(
            selectedIndustry = filterParameters.industry,
            onlyWithSalary = filterParameters.onlyWithSalary,
        )
    }

    fun onIndustrySelected(industry: FilterIndustry?) {
        filterSettingsInteractor.updateIndustry(industry)

        _uiState.value = _uiState.value.copy(
            selectedIndustry = industry
        )
    }

    fun onOnlyWithSalaryChanged(checked: Boolean) {
        filterSettingsInteractor.updateOnlyWithSalary(checked)

        _uiState.value = _uiState.value.copy(
            onlyWithSalary = checked
        )
    }

    fun onApplyClicked() {
        viewModelScope.launch {
            _navigationEvent.value = FilterUiState.NavigationEvent.NavigationBack
        }
    }

    fun onResetClicked() {
        filterSettingsInteractor.clearAllFilters()

        _uiState.value = FilterUiState(
            selectedIndustry = null,
            onlyWithSalary = false
        )
    }

    fun onNavigationEventHandled() {
        _navigationEvent.value = null
    }
}
