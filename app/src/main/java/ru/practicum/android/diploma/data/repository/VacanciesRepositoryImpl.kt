package ru.practicum.android.diploma.data.repository

import ru.practicum.android.diploma.R
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


    private val NO_INTERNET_ERROR = resourceProvider.getString(R.string.error_no_internet)
    private val SERVER_ERROR = resourceProvider.getString(R.string.error_server)
    private val DATA_FORMAT_ERROR = resourceProvider.getString(R.string.error_data_format)
    private val VACANCY_NOT_FOUND = resourceProvider.getString(R.string.error_vacancy_not_found)
    private val RESPONSE_IS_EMPTY = resourceProvider.getString(R.string.error_response_empty)


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

        return when (response.resultCode) {
            RetrofitNetworkClient.SUCCESS -> {
                val apiResponse = response.data ?: return Resource.Error(RESPONSE_IS_EMPTY)
                Resource.Success(
                    VacancyResponse(
                        found = apiResponse.found,
                        pages = apiResponse.pages,
                        page = apiResponse.page,
                        vacancies = apiResponse.items.map { it.toDomain() }
                    )
                )
            }

            RetrofitNetworkClient.NO_INTERNET_CONNECTION -> Resource.Error(NO_INTERNET_ERROR)
            404 -> Resource.Error(VACANCY_NOT_FOUND)
            else -> Resource.Error(SERVER_ERROR)
        }
    }

    override suspend fun getVacancyDetails(id: Int): Resource<VacancyDetail> {
        val response = networkClient.doRequest { vacanciesApi.getVacancyDetails(id) }
        return when (response.resultCode) {
            RetrofitNetworkClient.SUCCESS -> {
                val apiResponse = response.data ?: return Resource.Error(DATA_FORMAT_ERROR)
                Resource.Success(apiResponse.toDomain())
            }

            RetrofitNetworkClient.NO_INTERNET_CONNECTION -> Resource.Error(NO_INTERNET_ERROR)
            404 -> Resource.Error(VACANCY_NOT_FOUND)
            else -> Resource.Error(SERVER_ERROR)
        }
    }

    override suspend fun getAreas(): Resource<List<FilterArea>> {
        val response = networkClient.doRequest { vacanciesApi.getAreas() }
        return when (response.resultCode) {
            RetrofitNetworkClient.SUCCESS -> {
                val apiResponse = response.data ?: return Resource.Error(DATA_FORMAT_ERROR)
                Resource.Success(apiResponse.map { it.toDomain() })
            }

            RetrofitNetworkClient.NO_INTERNET_CONNECTION -> Resource.Error(NO_INTERNET_ERROR)
            else -> Resource.Error(SERVER_ERROR)
        }
    }


    override suspend fun getIndustries(): Resource<List<FilterIndustry>> {
        val response = networkClient.doRequest { vacanciesApi.getIndustries() }

        return when (response.resultCode) {
            RetrofitNetworkClient.SUCCESS -> {
                val apiResponse = response.data ?: return Resource.Error(DATA_FORMAT_ERROR)
                Resource.Success(apiResponse.map { it.toDomain() })
            }

            RetrofitNetworkClient.NO_INTERNET_CONNECTION -> Resource.Error(NO_INTERNET_ERROR)
            else -> Resource.Error(SERVER_ERROR)
        }
    }
}


