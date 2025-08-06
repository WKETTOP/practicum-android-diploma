package ru.practicum.android.diploma.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.practicum.android.diploma.data.dto.AreasRequest
import ru.practicum.android.diploma.data.dto.IndustriesRequest
import ru.practicum.android.diploma.data.dto.Response
import ru.practicum.android.diploma.data.dto.VacancyDetailRequest
import ru.practicum.android.diploma.data.dto.VacancySearchRequest

class RetrofitNetworkClient(
    private val context: Context,
    private val vacanciesApi: VacanciesApi
) : NetworkClient {

    companion object {
        const val NO_INTERNET_CONNECTION = -1
        const val BAD_REQUEST = 400
        const val SUCCESS = 200
        const val SERVER_ERROR = 500
    }

    override suspend fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = NO_INTERNET_CONNECTION }
        }

        return withContext(Dispatchers.IO) {
            try {
                when (dto) {
                    is VacancySearchRequest -> handleSearchRequest(dto)
                    is VacancyDetailRequest -> handleDetailRequest(dto)
                    is AreasRequest -> handleAreasRequest()
                    is IndustriesRequest -> handleIndustriesRequest()
                    else -> Response().apply { resultCode = BAD_REQUEST }
                }
            } catch (e: Exception) {
                Response().apply { resultCode = SERVER_ERROR }
            }
        }
    }

    private suspend fun handleSearchRequest(request: VacancySearchRequest): Response {
        val response = vacanciesApi.getVacancies(
            area = request.area,
            industry = request.industry,
            text = request.text,
            salary = request.salary,
            page = request.page,
            onlyWithSalary = request.onlyWithSalary
        )

        return if (response.isSuccessful && response.body() != null) {
            Response().apply {
                resultCode = SUCCESS
                data = response.body()
            }
        } else {
            Response().apply { resultCode = response.code() }
        }
    }

    private suspend fun handleDetailRequest(request: VacancyDetailRequest): Response {
        val response = vacanciesApi.getVacancyDetails(request.id)
        return if (response.isSuccessful && response.body() != null) {
            Response().apply {
                resultCode = SUCCESS
                data = response.body()!!
            }
        } else {
            Response().apply { resultCode = response.code() }
        }
    }

    private suspend fun handleAreasRequest(): Response {
        val response = vacanciesApi.getAreas()
        return if (response.isSuccessful && response.body() != null) {
            Response().apply {
                resultCode = SUCCESS
                data = response.body()!!
            }
        } else {
            Response().apply { resultCode = response.code() }
        }
    }

    private suspend fun handleIndustriesRequest(): Response {
        val response = vacanciesApi.getIndustries()
        return if (response.isSuccessful && response.body() != null) {
            Response().apply {
                resultCode = SUCCESS
                data = response.body()!!
            }
        } else {
            Response().apply { resultCode = response.code() }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val capabilities = connectivityManager.getNetworkCapabilities(
            connectivityManager.activeNetwork
        )

        return capabilities?.run {
            hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } ?: false
    }
}
