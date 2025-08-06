package ru.practicum.android.diploma.data.repository

import ru.practicum.android.diploma.data.dto.AreasRequest
import ru.practicum.android.diploma.data.dto.IndustriesRequest
import ru.practicum.android.diploma.data.dto.VacancyDetailRequest
import ru.practicum.android.diploma.data.dto.VacancySearchRequest
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.RetrofitNetworkClient
import ru.practicum.android.diploma.data.network.models.ApiFilterArea
import ru.practicum.android.diploma.data.network.models.ApiFilterIndustry
import ru.practicum.android.diploma.data.network.models.ApiVacancyDetail
import ru.practicum.android.diploma.data.network.models.ApiVacancySearchResponse
import ru.practicum.android.diploma.data.network.models.toDomain
import ru.practicum.android.diploma.domain.models.FilterArea
import ru.practicum.android.diploma.domain.models.FilterIndustry
import ru.practicum.android.diploma.domain.models.VacancyDetail
import ru.practicum.android.diploma.domain.models.VacancyResponse
import ru.practicum.android.diploma.domain.repository.VacanciesRepository
import ru.practicum.android.diploma.util.Resource

class VacanciesRepositoryImpl(
    private val networkClient: NetworkClient
) : VacanciesRepository {

    override suspend fun searchVacancies(
        area: Int?,
        industry: Int?,
        text: String?,
        salary: Int?,
        page: Int,
        onlyWithSalary: Boolean
    ): Resource<VacancyResponse> {
        val response = networkClient.doRequest(
            VacancySearchRequest(area, industry, text, salary, page, onlyWithSalary)
        )

        return when (response.resultCode) {
            RetrofitNetworkClient.SUCCESS -> {
                try {
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
                    } ?: Resource.Error("Неверный формат данных")
                } catch (e: Exception) {
                    Resource.Error("Ошибка обработки данных: ${e.message}")
                }
            }
            RetrofitNetworkClient.NO_INTERNET_CONNECTION -> Resource.Error("Нет интернет-соединения")
            else -> Resource.Error("Ошибка сервера: ${response.resultCode}")
        }
    }

    override suspend fun getVacancyDetails(id: Int): Resource<VacancyDetail> {
        val response = networkClient.doRequest(VacancyDetailRequest(id))
        return when (response.resultCode) {
            RetrofitNetworkClient.SUCCESS -> {
                try {
                    val detailResponse = response.data as? ApiVacancyDetail
                    detailResponse?.let {
                        Resource.Success(it.toDomain())
                    } ?: Resource.Error("Неверный формат данных вакансии")
                } catch (e: Exception) {
                    Resource.Error("Ошибка обработки данных вакансии: ${e.message}")
                }
            }
            404 -> Resource.Error("Вакансия не найдена")
            RetrofitNetworkClient.NO_INTERNET_CONNECTION -> Resource.Error("Нет интернет-соединения")
            else -> Resource.Error("Ошибка сервера: ${response.resultCode}")
        }
    }

    override suspend fun getAreas(): Resource<List<FilterArea>> {
        val response = networkClient.doRequest(AreasRequest())
        return when (response.resultCode) {
            RetrofitNetworkClient.SUCCESS -> {
                try {
                    val areasResponse = response.data as? List<ApiFilterArea>
                    areasResponse?.let {
                        Resource.Success(it.map { area -> area.toDomain() })
                    } ?: Resource.Error("Неверный формат данных регионов")
                } catch (e: Exception) {
                    Resource.Error("Ошибка обработки данных регионов: ${e.message}")
                }
            }
            RetrofitNetworkClient.NO_INTERNET_CONNECTION -> Resource.Error("Нет интернет-соединения")
            else -> Resource.Error("Ошибка сервера: ${response.resultCode}")
        }
    }

    override suspend fun getIndustries(): Resource<List<FilterIndustry>> {
        val response = networkClient.doRequest(IndustriesRequest())
        return when (response.resultCode) {
            RetrofitNetworkClient.SUCCESS -> {
                try {
                    val industriesResponse = response.data as? List<ApiFilterIndustry>
                    industriesResponse?.let {
                        Resource.Success(it.map { industry -> industry.toDomain() })
                    } ?: Resource.Error("Неверный формат данных отраслей")
                } catch (e: Exception) {
                    Resource.Error("Ошибка обработки данных отраслей: ${e.message}")
                }
            }
            RetrofitNetworkClient.NO_INTERNET_CONNECTION -> Resource.Error("Нет интернет-соединения")
            else -> Resource.Error("Ошибка сервера: ${response.resultCode}")
        }
    }
}
