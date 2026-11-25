package com.example.di

import com.example.data.repository.ZScouterRepository
import com.example.domain.repository.DragonBallRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<DragonBallRepository> {
        ZScouterRepository(
            api = get(),
            figureDao = get(),
            workoutDao = get()
        )
    }
}
