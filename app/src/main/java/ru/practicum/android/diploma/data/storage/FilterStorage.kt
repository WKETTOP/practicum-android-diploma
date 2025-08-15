package ru.practicum.android.diploma.data.storage

import android.content.SharedPreferences
import com.google.gson.Gson
import ru.practicum.android.diploma.domain.models.FilterParameters

class FilterStorage(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) {

    fun saveFilterParameters(parameters: FilterParameters) {
        val json = gson.toJson(parameters)
        sharedPreferences.edit()
            .putString(FILTER_PARAMETERS_KEY, json)
            .apply()
    }

    fun getFilterParameters(): FilterParameters {
        val json = sharedPreferences.getString(FILTER_PARAMETERS_KEY, null)
        return if (json != null) {
            try {
                gson.fromJson(json, FilterParameters::class.java)
            } catch (e: Exception) {
                FilterParameters()
            }
        } else {
            FilterParameters()
        }
    }

    fun clearFilterParameters() {
        sharedPreferences.edit()
            .remove(FILTER_PARAMETERS_KEY)
            .apply()
    }

    companion object {
        private const val FILTER_PARAMETERS_KEY = "filter_parameters"
    }
}
