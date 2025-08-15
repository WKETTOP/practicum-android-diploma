package ru.practicum.android.diploma.domain.models

data class FilterParameters(
    val industry: FilterIndustry? = null,
    val onlyWithSalary: Boolean = false
) {
    fun hasActiveFilters(): Boolean {
        return industry != null || onlyWithSalary
    }
}
