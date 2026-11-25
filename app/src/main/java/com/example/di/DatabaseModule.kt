package com.example.di

import com.example.data.local.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { AppDatabase.getDatabase(androidContext()) }
    single { get<AppDatabase>().characterDao() }
    single { get<AppDatabase>().planetDao() }
    single { get<AppDatabase>().figureDao() }
    single { get<AppDatabase>().workoutDao() }
}
