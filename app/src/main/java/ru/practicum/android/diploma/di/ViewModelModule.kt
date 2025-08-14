package ru.practicum.android.diploma.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.presentation.viewmodel.MainViewModel
import ru.practicum.android.diploma.presentation.viewmodel.VacancyViewModel

val viewModelModule = module {

    viewModel {
        MainViewModel(get(),get())
    }

    viewModel {
        VacancyViewModel(get())
    }
}
