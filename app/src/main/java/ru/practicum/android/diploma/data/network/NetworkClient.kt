package ru.practicum.android.diploma.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {

    private const val BASE_URL = "https://practicum-diploma-8bc38133faba.herokuapp.com/"
    private const val CONNECT_TIME = 30L
    private const val READ_TIME = 30L

    // Для прохождения аутентификации
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .addHeader("Authorization", "BuildConfig.API_ACCESS_TOKEN(должен быть токен, ток хз как взять)")
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(request)
        }
        .connectTimeout(CONNECT_TIME, TimeUnit.SECONDS)
        .readTimeout(READ_TIME, TimeUnit.SECONDS)
        .build()

    private val client: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            ).build()
    }

    val api: VacanciesApi = client.create(VacanciesApi::class.java)

}
