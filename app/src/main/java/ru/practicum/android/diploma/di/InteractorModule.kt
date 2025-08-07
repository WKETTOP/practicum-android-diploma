package ru.practicum.android.diploma.di

import org.koin.dsl.module
import ru.practicum.android.diploma.domain.usecase.GetAreasUseCase
import ru.practicum.android.diploma.domain.usecase.GetIndustriesUseCase
import ru.practicum.android.diploma.domain.usecase.GetVacanciesUseCase
import ru.practicum.android.diploma.domain.usecase.GetVacancyDetailsUseCase

val interactorModule = module {
    factory { GetVacanciesUseCase(get()) }
    factory { GetVacancyDetailsUseCase(get()) }
    factory { GetAreasUseCase(get()) }
    factory { GetIndustriesUseCase(get()) }
}
