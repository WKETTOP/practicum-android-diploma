package ru.practicum.android.diploma.data.storage

import android.content.SharedPreferences
import androidx.core.content.edit
import ru.practicum.android.diploma.domain.models.FilterIndustry
import ru.practicum.android.diploma.domain.models.FilterParameters

class FilterStorage(
    private val sharedPreferences: SharedPreferences
) {

    fun saveFilterParameters(parameters: FilterParameters) {
        sharedPreferences.edit {
            if (parameters.industry != null) {
                putInt(INDUSTRY_ID_KEY, parameters.industry.id)
                putString(INDUSTRY_NAME_KEY, parameters.industry.name)
            } else {
                remove(INDUSTRY_ID_KEY)
                remove(INDUSTRY_NAME_KEY)
            }

            putBoolean(ONLY_WITH_SALARY_KEY, parameters.onlyWithSalary)
        }
    }

    fun getFilterParameters(): FilterParameters {
        val industryId = sharedPreferences.getInt(INDUSTRY_ID_KEY, -1)
        val industryName = sharedPreferences.getString(INDUSTRY_NAME_KEY, null)

        val industry = if (industryId != -1 && industryName != null) {
            FilterIndustry(industryId, industryName)
        } else {
            null
        }

        val onlyWithSalary = sharedPreferences.getBoolean(ONLY_WITH_SALARY_KEY, false)

        return FilterParameters(
            industry = industry,
            onlyWithSalary = onlyWithSalary
        )
    }

    fun clearFilterParameters() {
        sharedPreferences.edit {
            remove(INDUSTRY_ID_KEY)
            remove(INDUSTRY_NAME_KEY)
            remove(ONLY_WITH_SALARY_KEY)
        }
    }

    companion object {
        private const val INDUSTRY_ID_KEY = "industry_id"
        private const val INDUSTRY_NAME_KEY = "industry_name"
        private const val ONLY_WITH_SALARY_KEY = "only_with_salary"
    }
}
