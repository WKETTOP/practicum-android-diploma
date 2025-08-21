package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.domain.impl.FilterSettingsInteractorImpl
import ru.practicum.android.diploma.domain.interactor.FilterSettingsInteractor
import ru.practicum.android.diploma.domain.usecase.GetAreasUseCase
import ru.practicum.android.diploma.domain.usecase.GetIndustriesUseCase
import ru.practicum.android.diploma.domain.usecase.GetVacanciesUseCase
import ru.practicum.android.diploma.domain.usecase.GetVacancyDetailsUseCase

val interactorModule = module {
    factory { GetVacanciesUseCase(get()) }
    factory { GetVacancyDetailsUseCase(get()) }
    factory { GetAreasUseCase(get()) }
    factory { GetIndustriesUseCase(get()) }

    factory<FilterSettingsInteractor> {
        FilterSettingsInteractorImpl(get())
    }
}
