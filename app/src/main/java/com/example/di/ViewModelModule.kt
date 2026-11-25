package com.example.di

import com.example.ui.viewmodel.ZScouterViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        ZScouterViewModel(
            getCharactersUseCase = get(),
            getCharacterDetailUseCase = get(),
            getPlanetsUseCase = get(),
            filterCharactersUseCase = get(),
            calculateSaiyanRankUseCase = get(),
            manageFiguresUseCase = get(),
            manageWorkoutsUseCase = get(),
            simulateBattleUseCase = get(),
            isOfflineModeFlow = get<com.example.domain.repository.DragonBallRepository>().isOfflineMode
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
