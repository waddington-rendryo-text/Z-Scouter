package com.example.di

import com.example.domain.usecase.CalculateSaiyanRankUseCase
import com.example.domain.usecase.FilterCharactersUseCase
import com.example.domain.usecase.GetCharacterDetailUseCase
import com.example.domain.usecase.GetCharactersUseCase
import com.example.domain.usecase.GetPlanetsUseCase
import com.example.domain.usecase.ManageFiguresUseCase
import com.example.domain.usecase.ManageWorkoutsUseCase
import com.example.domain.usecase.SimulateBattleUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetCharactersUseCase(repository = get()) }
    factory { GetCharacterDetailUseCase(repository = get()) }
    factory { GetPlanetsUseCase(repository = get()) }
    factory { FilterCharactersUseCase() }
    factory { CalculateSaiyanRankUseCase() }
    factory { ManageFiguresUseCase(repository = get()) }
    factory {
        ManageWorkoutsUseCase(
            repository = get(),
            calculateSaiyanRankUseCase = get()
        )
    }
    factory { SimulateBattleUseCase() }
}
