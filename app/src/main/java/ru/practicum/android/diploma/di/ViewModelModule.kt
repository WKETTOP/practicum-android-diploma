package ru.practicum.android.diploma.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.diploma.presentation.MainViewModel

val viewModelModule = module {

    viewModel {
        MainViewModel(get())
    }
}
