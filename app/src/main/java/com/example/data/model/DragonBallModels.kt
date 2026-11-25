package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CharactersResponse(
    @Json(name = "items") val items: List<CharacterDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class CharacterDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "ki") val ki: String? = null,
    @Json(name = "maxKi") val maxKi: String? = null,
    @Json(name = "race") val race: String? = null,
    @Json(name = "gender") val gender: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "image") val image: String? = null,
    @Json(name = "affiliation") val affiliation: String? = null
)

@JsonClass(generateAdapter = true)
data class TransformationDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "image") val image: String? = null,
    @Json(name = "ki") val ki: String? = null
)

@JsonClass(generateAdapter = true)
data class OriginPlanetDto(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "isDestroyed") val isDestroyed: Boolean? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "image") val image: String? = null
)

@JsonClass(generateAdapter = true)
data class CharacterDetailDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "ki") val ki: String? = null,
    @Json(name = "maxKi") val maxKi: String? = null,
    @Json(name = "race") val race: String? = null,
    @Json(name = "gender") val gender: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "image") val image: String? = null,
    @Json(name = "affiliation") val affiliation: String? = null,
    @Json(name = "originPlanet") val originPlanet: OriginPlanetDto? = null,
    @Json(name = "transformations") val transformations: List<TransformationDto>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class PlanetDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "isDestroyed") val isDestroyed: Boolean? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "image") val image: String? = null
)

@JsonClass(generateAdapter = true)
data class PlanetsResponse(
    @Json(name = "items") val items: List<PlanetDto> = emptyList()
)
