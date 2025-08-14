package ru.practicum.android.diploma.di

import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.practicum.android.diploma.BuildConfig
import ru.practicum.android.diploma.data.network.NetworkClient
import ru.practicum.android.diploma.data.network.RetrofitNetworkClient
import ru.practicum.android.diploma.data.network.VacanciesApi
import ru.practicum.android.diploma.data.repository.ResourceProviderImpl
import ru.practicum.android.diploma.domain.repository.ResourceProvider

val dataModule = module {
    single<VacanciesApi> {
        Retrofit.Builder()
            .baseUrl("https://practicum-diploma-8bc38133faba.herokuapp.com/")
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer ${BuildConfig.API_ACCESS_TOKEN}")
                            .addHeader("Accept", "application/json")
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VacanciesApi::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(androidContext())
    }

    single<ResourceProvider> { ResourceProviderImpl(androidContext()) }
}
