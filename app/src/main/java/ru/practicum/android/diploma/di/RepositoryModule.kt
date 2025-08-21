package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.data.repository.FilterSettingsRepositoryImpl
import ru.practicum.android.diploma.data.repository.VacanciesRepositoryImpl
import ru.practicum.android.diploma.domain.repository.FilterSettingsRepository
import ru.practicum.android.diploma.domain.repository.VacanciesRepository

val repositoryModule = module {
    single<VacanciesRepository> {
        VacanciesRepositoryImpl(
            networkClient = get(),
            vacanciesApi = get(),
        )
    }

    single<FilterSettingsRepository> {
        FilterSettingsRepositoryImpl(get())
    }
}
