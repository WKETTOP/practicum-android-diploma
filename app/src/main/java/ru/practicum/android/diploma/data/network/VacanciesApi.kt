package ru.practicum.android.diploma.data.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.practicum.android.diploma.data.network.models.FilterArea
import ru.practicum.android.diploma.data.network.models.FilterIndustry
import ru.practicum.android.diploma.data.network.models.VacancyDetail
import ru.practicum.android.diploma.data.network.models.VacancyResponse

interface VacanciesApi {
    @GET("/areas")
    suspend fun getAreas(): Response<List<FilterArea>>

    @GET("/industries")
    suspend fun getIndustries(): Response<List<FilterIndustry>>

    @GET("/vacancies")
    suspend fun getVacancies(
        @Query("area") area: Int?,
        @Query("industry") industry: Int?,
        @Query("text") text: String?,
        @Query("salary") salary: Int?,
        @Query("page") page: Int = 1,
        @Query("only_with_salary") onlyWithSalary: Boolean = false
    ): Response<VacancyResponse>

    @GET("/vacancies/{id}")
    suspend fun getVacancyDetails(
        @Path("id") id: Int
    ): Response<VacancyDetail>
}
