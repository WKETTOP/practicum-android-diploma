package ru.practicum.android.diploma.data.storage

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import ru.practicum.android.diploma.domain.models.FilterParameters

class FilterStorage(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) {

    fun saveFilterParameters(parameters: FilterParameters) {
        val json = gson.toJson(parameters)
        sharedPreferences.edit {
            putString(FILTER_PARAMETERS_KEY, json)
        }
    }

    fun getFilterParameters(): FilterParameters {
        val json = sharedPreferences.getString(FILTER_PARAMETERS_KEY, null)
        return if (json != null) {
            try {
                gson.fromJson(json, FilterParameters::class.java)
            } catch (e: JsonSyntaxException) {
                Log.w(TAG, "Invalid JSON format in filter parameters: ${e.message}")
                clearFilterParameters()
                FilterParameters()
            } catch (e: IllegalStateException) {
                Log.w(TAG, "Gson state error while parsing filter parameters: ${e.message}")
                FilterParameters()
            }
        } else {
            FilterParameters()
        }
    }

    fun clearFilterParameters() {
        sharedPreferences.edit {
            remove(FILTER_PARAMETERS_KEY)
        }
    }

    companion object {
        private const val FILTER_PARAMETERS_KEY = "filter_parameters"
        private const val TAG = "FilterStorage"
    }
}
