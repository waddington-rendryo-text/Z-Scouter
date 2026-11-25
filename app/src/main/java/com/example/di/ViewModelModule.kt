package com.example.di

import com.example.ui.viewmodel.ZScouterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        ZScouterViewModel(
            repository = get(),
            filterCharactersUseCase = get(),
            calculateSaiyanRankUseCase = get(),
            simulateBattleUseCase = get()
        )
    }
}

val appModules = listOf(
    networkModule,
    databaseModule,
    repositoryModule,
    domainModule,
    viewModelModule
)
