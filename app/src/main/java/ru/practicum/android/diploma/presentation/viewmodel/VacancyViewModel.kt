package ru.practicum.android.diploma.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.practicum.android.diploma.domain.models.Resource
import ru.practicum.android.diploma.domain.models.VacancyDetail
import ru.practicum.android.diploma.domain.usecase.GetVacancyDetailsUseCase

class VacancyViewModel(private val getVacancyDetailsUseCase: GetVacancyDetailsUseCase) : ViewModel() {

    private val _vacancyState = MutableLiveData<Resource<VacancyDetail>>()
    val vacancyState: LiveData<Resource<VacancyDetail>> = _vacancyState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadVacancy(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _vacancyState.value = Resource.Loading<VacancyDetail>()

            val result = getVacancyDetailsUseCase(id)
            _vacancyState.value = result
            _isLoading.value = false
        }
    }
}
