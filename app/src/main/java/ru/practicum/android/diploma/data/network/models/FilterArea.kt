package ru.practicum.android.diploma.data.network.models

data class FilterArea(
    val id: Int,
    val name: String,
    val parentId: Int?,
    val areas: List<FilterArea>
)
