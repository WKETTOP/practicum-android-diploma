package ru.practicum.android.diploma.data.repository

import android.util.Log
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.data.dto.Response
import ru.practicum.android.diploma.data.mappers.toDomain
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.ResourceProvider
import ru.practicum.android.diploma.data.network.RetrofitNetworkClient
import ru.practicum.android.diploma.data.network.VacanciesApi
import ru.practicum.android.diploma.domain.models.FilterArea
import ru.practicum.android.diploma.domain.models.FilterIndustry
import ru.practicum.android.diploma.domain.models.Resource
import ru.practicum.android.diploma.domain.models.SearchParams
import ru.practicum.android.diploma.domain.models.VacancyDetail
import ru.practicum.android.diploma.domain.models.VacancyResponse
import ru.practicum.android.diploma.domain.repository.VacanciesRepository

class VacanciesRepositoryImpl(
    private val networkClient: NetworkClient,
    private val vacanciesApi: VacanciesApi,
    private val resourceProvider: ResourceProvider
) : VacanciesRepository {

    companion object {
        const val TAG = "VacanciesRepository"
        const val NOT_FOUND = 404
    }

    private val errorNoInternet = resourceProvider.getString(R.string.error_no_internet)
    private val errorServer = resourceProvider.getString(R.string.error_server)
    private val errorDataFormat = resourceProvider.getString(R.string.error_data_format)
    private val errorVacancyNotFound = resourceProvider.getString(R.string.error_vacancy_not_found)
    private val errorResponseEmpty = resourceProvider.getString(R.string.error_response_empty)

    override suspend fun searchVacancies(params: SearchParams): Resource<VacancyResponse> {
        val response = networkClient.doRequest {
            vacanciesApi.getVacancies(
                area = params.area,
                industry = params.industry,
                text = params.text,
                salary = params.salary,
                page = params.page,
                onlyWithSalary = params.onlyWithSalary
            )
        }

        return handleResponse(
            response,
            onSuccess = { apiResponse ->
                Resource.Success(
                    VacancyResponse(
                        found = apiResponse.found,
                        pages = apiResponse.pages,
                        page = apiResponse.page,
                        vacancies = apiResponse.items.map { it.toDomain() }
                    )
                )
            },
            onNotFound = { Resource.Error(errorVacancyNotFound) }
        )
    }

    override suspend fun getVacancyDetails(id: String): Resource<VacancyDetail> {
        val response = networkClient.doRequest { vacanciesApi.getVacancyDetails(id) }

        return handleResponse(
            response,
            onSuccess = { apiResponse -> Resource.Success(apiResponse.toDomain()) },
            onNotFound = { Resource.Error(errorVacancyNotFound) }
        )
    }

    override suspend fun getAreas(): Resource<List<FilterArea>> {
        val response = networkClient.doRequest { vacanciesApi.getAreas() }

        return handleResponse(
            response,
            onSuccess = { apiResponse ->
                Resource.Success(apiResponse.map { it.toDomain() })
            }
        )
    }

    override suspend fun getIndustries(): Resource<List<FilterIndustry>> {
        val response = networkClient.doRequest { vacanciesApi.getIndustries() }

        return handleResponse(
            response,
            onSuccess = { apiResponse ->
                Resource.Success(apiResponse.map { it.toDomain() })
            }
        )
    }

    private fun <T, R> handleResponse(
        response: Response<T>,
        onSuccess: (T) -> Resource<R>,
        onNotFound: (() -> Resource<R>)? = null
    ): Resource<R> {
        return when (response.resultCode) {
            RetrofitNetworkClient.SUCCESS -> {
                val data = response.data
                if (data != null) {
                    try {
                        onSuccess(data)
                    } catch (e: IllegalArgumentException) {
                        Log.e(TAG, errorDataFormat, e)
                        Resource.Error(errorDataFormat)
                    }
                } else {
                    Resource.Error(errorResponseEmpty)
                }
            }
            RetrofitNetworkClient.NO_INTERNET_CONNECTION -> Resource.Error(errorNoInternet)
            NOT_FOUND -> onNotFound?.invoke() ?: Resource.Error(errorResponseEmpty)
            else -> Resource.Error(errorServer)
        }
    }
}
