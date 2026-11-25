package com.example.di

import com.example.data.mapper.DragonBallMapper
import com.example.data.repository.ZScouterRepository
import com.example.domain.repository.DragonBallRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { DragonBallMapper(moshi = get()) }

    single<DragonBallRepository> {
        ZScouterRepository(
            api = get(),
            characterDao = get(),
            planetDao = get(),
            figureDao = get(),
            workoutDao = get(),
            mapper = get()
        )
    }
}
