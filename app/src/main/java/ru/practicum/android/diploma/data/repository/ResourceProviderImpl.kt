package ru.practicum.android.diploma.data.repository

import android.content.Context
import ru.practicum.android.diploma.domain.repository.ResourceProvider

class ResourceProviderImpl(private val context: Context) : ResourceProvider {
    override fun getString(id: Int) = context.getString(id)
}
