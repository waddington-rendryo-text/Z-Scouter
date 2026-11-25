package com.example.di

import com.example.domain.usecase.CalculateSaiyanRankUseCase
import com.example.domain.usecase.FilterCharactersUseCase
import com.example.domain.usecase.SimulateBattleUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { FilterCharactersUseCase() }
    factory { CalculateSaiyanRankUseCase() }
    factory { SimulateBattleUseCase() }
}
