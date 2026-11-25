package com.example.data.mapper

import com.example.data.local.CharacterEntity
import com.example.data.local.PlanetEntity
import com.example.data.model.CharacterDetailDto
import com.example.data.model.CharacterDto
import com.example.data.model.OriginPlanetDto
import com.example.data.model.PlanetDto
import com.example.data.model.TransformationDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class DragonBallMapper(private val moshi: Moshi) {

    private val originPlanetAdapter = moshi.adapter(OriginPlanetDto::class.java)
    private val transformationsListType = Types.newParameterizedType(List::class.java, TransformationDto::class.java)
    private val transformationsAdapter = moshi.adapter<List<TransformationDto>>(transformationsListType)

    fun toEntity(
        dto: CharacterDto,
        existingDetailCache: CharacterEntity? = null
    ): CharacterEntity {
        return CharacterEntity(
            id = dto.id,
            name = dto.name,
            ki = dto.ki,
            maxKi = dto.maxKi,
            race = dto.race,
            gender = dto.gender,
            description = dto.description,
            image = dto.image,
            affiliation = dto.affiliation,
            originPlanetJson = existingDetailCache?.originPlanetJson,
            transformationsJson = existingDetailCache?.transformationsJson,
            lastUpdatedAt = System.currentTimeMillis()
        )
    }

    fun toEntity(detailDto: CharacterDetailDto): CharacterEntity {
        val planetJson = detailDto.originPlanet?.let { originPlanetAdapter.toJson(it) }
        val transformationsJson = detailDto.transformations?.let { transformationsAdapter.toJson(it) }
        return CharacterEntity(
            id = detailDto.id,
            name = detailDto.name,
            ki = detailDto.ki,
            maxKi = detailDto.maxKi,
            race = detailDto.race,
            gender = detailDto.gender,
            description = detailDto.description,
            image = detailDto.image,
            affiliation = detailDto.affiliation,
            originPlanetJson = planetJson,
            transformationsJson = transformationsJson,
            lastUpdatedAt = System.currentTimeMillis()
        )
    }

    fun toCharacterDto(entity: CharacterEntity): CharacterDto {
        return CharacterDto(
            id = entity.id,
            name = entity.name,
            ki = entity.ki,
            maxKi = entity.maxKi,
            race = entity.race,
            gender = entity.gender,
            description = entity.description,
            image = entity.image,
            affiliation = entity.affiliation
        )
    }

    fun toCharacterDetailDto(entity: CharacterEntity): CharacterDetailDto {
        val originPlanet = entity.originPlanetJson?.let {
            runCatching { originPlanetAdapter.fromJson(it) }.getOrNull()
        }
        val transformations = entity.transformationsJson?.let {
            runCatching { transformationsAdapter.fromJson(it) }.getOrNull()
        } ?: emptyList()

        return CharacterDetailDto(
            id = entity.id,
            name = entity.name,
            ki = entity.ki,
            maxKi = entity.maxKi,
            race = entity.race,
            gender = entity.gender,
            description = entity.description,
            image = entity.image,
            affiliation = entity.affiliation,
            originPlanet = originPlanet,
            transformations = transformations
        )
    }

    fun toEntity(dto: PlanetDto): PlanetEntity {
        return PlanetEntity(
            id = dto.id,
            name = dto.name,
            isDestroyed = dto.isDestroyed,
            description = dto.description,
            image = dto.image,
            lastUpdatedAt = System.currentTimeMillis()
        )
    }

    fun toPlanetDto(entity: PlanetEntity): PlanetDto {
        return PlanetDto(
            id = entity.id,
            name = entity.name,
            isDestroyed = entity.isDestroyed,
            description = entity.description,
            image = entity.image
        )
    }
}
