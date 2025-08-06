package ru.practicum.android.diploma.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.practicum.android.diploma.data.network.models.ApiFilterArea
import ru.practicum.android.diploma.data.network.models.ApiFilterIndustry
import ru.practicum.android.diploma.data.network.models.ApiVacancyDetail
import ru.practicum.android.diploma.data.network.models.ApiVacancySearchResponse

@Suppress("LongParameterList")
interface VacanciesApi {
    @GET("/vacancies")
    suspend fun getVacancies(
        @Query("area") area: Int?,
        @Query("industry") industry: Int?,
        @Query("text") text: String?,
        @Query("salary") salary: Int?,
        @Query("page") page: Int,
        @Query("only_with_salary") onlyWithSalary: Boolean
    ): Response<ApiVacancySearchResponse>

    @GET("/vacancies/{id}")
    suspend fun getVacancyDetails(@Path("id") id: Int): Response<ApiVacancyDetail>

    @GET("/areas")
    suspend fun getAreas(): Response<List<ApiFilterArea>>

    @GET("/industries")
    suspend fun getIndustries(): Response<List<ApiFilterIndustry>>
}
