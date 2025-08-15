package ru.practicum.android.diploma.domain.models

data class FilterParameters(
    val industry: FilterIndustry? = null,
    val onlyWithSalary: Boolean = false
)

fun FilterParameters.hasActiveFilters(): Boolean {
    return industry != null || onlyWithSalary
}
