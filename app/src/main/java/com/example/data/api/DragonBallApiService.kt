package com.example.data.api

import com.example.data.model.CharacterDetailDto
import com.example.data.model.CharactersResponse
import com.example.data.model.PlanetsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DragonBallApiService {

    @GET("characters")
    suspend fun getCharacters(
        @Query("limit") limit: Int = 100
    ): CharactersResponse

    @GET("characters/{id}")
    suspend fun getCharacterDetail(
        @Path("id") id: Int
    ): CharacterDetailDto

    @GET("planets")
    suspend fun getPlanets(
        @Query("limit") limit: Int = 50
    ): PlanetsResponse
}
