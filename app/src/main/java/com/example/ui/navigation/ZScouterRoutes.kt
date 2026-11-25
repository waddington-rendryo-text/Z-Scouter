package com.example.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object RadarRoute

@Serializable
data class CharacterDetailRoute(val characterId: Int)

@Serializable
data object SaiyanModeRoute

@Serializable
data object SparkingBattleRoute

@Serializable
data object FiguresShelfRoute
