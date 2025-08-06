package ru.practicum.android.diploma.data.repository

import android.util.Log
import ru.practicum.android.diploma.data.dto.AreasRequest
import ru.practicum.android.diploma.data.dto.IndustriesRequest
import ru.practicum.android.diploma.data.dto.Response
import ru.practicum.android.diploma.data.dto.VacancyDetailRequest
import ru.practicum.android.diploma.data.dto.VacancySearchRequest
import ru.practicum.android.diploma.data.mappers.toDomain
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.RetrofitNetworkClient
import ru.practicum.android.diploma.data.network.models.ApiFilterArea
import ru.practicum.android.diploma.data.network.models.ApiFilterIndustry
import ru.practicum.android.diploma.data.network.models.ApiVacancyDetail
import ru.practicum.android.diploma.data.network.models.ApiVacancySearchResponse
import ru.practicum.android.diploma.domain.models.FilterArea
import ru.practicum.android.diploma.domain.models.FilterIndustry
import ru.practicum.android.diploma.domain.models.SearchParams
import ru.practicum.android.diploma.domain.models.VacancyDetail
import ru.practicum.android.diploma.domain.models.VacancyResponse
import ru.practicum.android.diploma.domain.repository.VacanciesRepository
import ru.practicum.android.diploma.util.Resource

class VacanciesRepositoryImpl(
    private val networkClient: NetworkClient
) : VacanciesRepository {

    companion object {
        private const val TAG = "VacanciesRepositoryImpl"
        private const val NO_INTERNET_ERROR = "Нет подключения к интернету"
        private const val SERVER_ERROR = "Ошибка сервера"
        private const val DATA_FORMAT_ERROR = "Неверный формат данных"
        private const val VACANCY_NOT_FOUND = "Вакансия не найдена"
        const val BAD_REQUEST = 400
    }

    override suspend fun searchVacancies(
        params: SearchParams
    ): Resource<VacancyResponse> {
        val response = networkClient.doRequest(
            VacancySearchRequest(
                area = params.area,
                industry = params.industry,
                text = params.text,
                salary = params.salary,
                page = params.page,
                onlyWithSalary = params.onlyWithSalary
            )
        )

        return when (response.resultCode) {
            RetrofitNetworkClient.SUCCESS -> processVacancySearchResponse(response)
            RetrofitNetworkClient.NO_INTERNET_CONNECTION -> Resource.Error(NO_INTERNET_ERROR)
            else -> Resource.Error(SERVER_ERROR)
        }
    }

    private fun processVacancySearchResponse(response: Response): Resource<VacancyResponse> {
        return try {
            val searchResponse = response.data as? ApiVacancySearchResponse
            searchResponse?.let {
                Resource.Success(
                    VacancyResponse(
                        found = it.found,
                        pages = it.pages,
                        page = it.page,
                        vacancies = it.items.map { item -> item.toDomain() }
                    )
                )
            } ?: Resource.Error(DATA_FORMAT_ERROR)
        } catch (e: ClassCastException) {
            Log.e(TAG, "Ошибка приведения типа в processVacancySearchResponse", e)
            Resource.Error(DATA_FORMAT_ERROR)
        }
    }

    override suspend fun getVacancyDetails(id: Int): Resource<VacancyDetail> {
        val response = networkClient.doRequest(VacancyDetailRequest(id))
        return when (response.resultCode) {
            RetrofitNetworkClient.SUCCESS -> processVacancyDetailResponse(response)
            BAD_REQUEST -> Resource.Error(VACANCY_NOT_FOUND)
            RetrofitNetworkClient.NO_INTERNET_CONNECTION -> Resource.Error(NO_INTERNET_ERROR)
            else -> Resource.Error(SERVER_ERROR)
        }
    }

    private fun processVacancyDetailResponse(response: Response): Resource<VacancyDetail> {
        return try {
            val detailResponse = response.data as? ApiVacancyDetail
            detailResponse?.let {
                Resource.Success(it.toDomain())
            } ?: Resource.Error(DATA_FORMAT_ERROR)
        } catch (e: ClassCastException) {
            Log.e(TAG, "Ошибка приведения типа в processVacancyDetailResponse", e)
            Resource.Error(DATA_FORMAT_ERROR)
        }
    }

    override suspend fun getAreas(): Resource<List<FilterArea>> {
        val response = networkClient.doRequest(AreasRequest())
        return when (response.resultCode) {
            RetrofitNetworkClient.SUCCESS -> processAreasResponse(response)
            RetrofitNetworkClient.NO_INTERNET_CONNECTION -> Resource.Error(NO_INTERNET_ERROR)
            else -> Resource.Error(SERVER_ERROR)
        }
    }

    private fun processAreasResponse(response: Response): Resource<List<FilterArea>> {
        return try {
            val areasResponse = response.data as? List<ApiFilterArea>
            areasResponse?.let {
                Resource.Success(it.map { area -> area.toDomain() })
            } ?: Resource.Error(DATA_FORMAT_ERROR)
        } catch (e: ClassCastException) {
            Log.e(TAG, "Ошибка приведения типа в processAreasResponse", e)
            Resource.Error(DATA_FORMAT_ERROR)
        }
    }

    override suspend fun getIndustries(): Resource<List<FilterIndustry>> {
        val response = networkClient.doRequest(IndustriesRequest())
        return when (response.resultCode) {
            RetrofitNetworkClient.SUCCESS -> processIndustriesResponse(response)
            RetrofitNetworkClient.NO_INTERNET_CONNECTION -> Resource.Error(NO_INTERNET_ERROR)
            else -> Resource.Error(SERVER_ERROR)
        }
    }

    private fun processIndustriesResponse(response: Response): Resource<List<FilterIndustry>> {
        return try {
            val industriesResponse = response.data as? List<ApiFilterIndustry>
            industriesResponse?.let {
                Resource.Success(it.map { industry -> industry.toDomain() })
            } ?: Resource.Error(DATA_FORMAT_ERROR)
        } catch (e: ClassCastException) {
            Log.e(TAG, "Ошибка приведения типа в processIndustriesResponse", e)
            Resource.Error(DATA_FORMAT_ERROR)
        }
    }
}
