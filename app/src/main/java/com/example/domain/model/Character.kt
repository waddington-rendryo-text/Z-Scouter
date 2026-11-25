package com.example.domain.model

data class Character(
    val id: Int,
    val name: String,
    val ki: String? = null,
    val maxKi: String? = null,
    val race: String? = null,
    val gender: String? = null,
    val description: String? = null,
    val image: String? = null,
    val affiliation: String? = null
) {
    val isSaiyan: Boolean
        get() = race.equals("Saiyan", ignoreCase = true)

    val isPrinceVegeta: Boolean
        get() = name.contains("Vegeta", ignoreCase = true)
}

data class Transformation(
    val id: Int,
    val name: String,
    val image: String? = null,
    val ki: String? = null
)

data class OriginPlanet(
    val id: Int? = null,
    val name: String? = null,
    val isDestroyed: Boolean? = null,
    val description: String? = null,
    val image: String? = null
)

data class CharacterDetail(
    val id: Int,
    val name: String,
    val ki: String? = null,
    val maxKi: String? = null,
    val race: String? = null,
    val gender: String? = null,
    val description: String? = null,
    val image: String? = null,
    val affiliation: String? = null,
    val originPlanet: OriginPlanet? = null,
    val transformations: List<Transformation> = emptyList()
) {
    fun toBasicCharacter(): Character = Character(
        id = id,
        name = name,
        ki = ki,
        maxKi = maxKi,
        race = race,
        gender = gender,
        description = description,
        image = image,
        affiliation = affiliation
    )
}

data class Planet(
    val id: Int,
    val name: String,
    val isDestroyed: Boolean? = null,
    val description: String? = null,
    val image: String? = null
)
