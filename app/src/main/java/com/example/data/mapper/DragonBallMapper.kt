package com.example.data.mapper

import com.example.data.local.CharacterEntity
import com.example.data.local.FigureEntity
import com.example.data.local.PlanetEntity
import com.example.data.local.WorkoutLogEntity
import com.example.data.model.CharacterDetailDto
import com.example.data.model.CharacterDto
import com.example.data.model.OriginPlanetDto
import com.example.data.model.PlanetDto
import com.example.data.model.TransformationDto
import com.example.domain.model.ActionFigure
import com.example.domain.model.Character
import com.example.domain.model.CharacterDetail
import com.example.domain.model.FigureStatus
import com.example.domain.model.OriginPlanet
import com.example.domain.model.Planet
import com.example.domain.model.Transformation
import com.example.domain.model.WorkoutLog
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class DragonBallMapper(moshi: Moshi) {

    private val originPlanetAdapter = moshi.adapter(OriginPlanetDto::class.java)
    private val transformationsListType = Types.newParameterizedType(List::class.java, TransformationDto::class.java)
    private val transformationsAdapter = moshi.adapter<List<TransformationDto>>(transformationsListType)

    // --- DTO <-> Entity ---

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

    // --- DTO / Entity -> Pure Domain ---

    fun toDomain(dto: CharacterDto): Character {
        return Character(
            id = dto.id,
            name = dto.name,
            ki = dto.ki,
            maxKi = dto.maxKi,
            race = dto.race,
            gender = dto.gender,
            description = dto.description,
            image = dto.image,
            affiliation = dto.affiliation
        )
    }

    fun toDomain(entity: CharacterEntity): Character {
        return Character(
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

    fun toDomain(dto: CharacterDetailDto): CharacterDetail {
        return CharacterDetail(
            id = dto.id,
            name = dto.name,
            ki = dto.ki,
            maxKi = dto.maxKi,
            race = dto.race,
            gender = dto.gender,
            description = dto.description,
            image = dto.image,
            affiliation = dto.affiliation,
            originPlanet = dto.originPlanet?.let {
                OriginPlanet(
                    id = it.id,
                    name = it.name,
                    isDestroyed = it.isDestroyed,
                    description = it.description,
                    image = it.image
                )
            },
            transformations = dto.transformations.orEmpty().map {
                Transformation(
                    id = it.id,
                    name = it.name,
                    image = it.image,
                    ki = it.ki
                )
            }
        )
    }

    fun toCharacterDetailDomain(entity: CharacterEntity): CharacterDetail {
        val originPlanet = entity.originPlanetJson?.let {
            runCatching { originPlanetAdapter.fromJson(it) }.getOrNull()
        }?.let {
            OriginPlanet(
                id = it.id,
                name = it.name,
                isDestroyed = it.isDestroyed,
                description = it.description,
                image = it.image
            )
        }

        val transformations = entity.transformationsJson?.let {
            runCatching { transformationsAdapter.fromJson(it) }.getOrNull()
        }.orEmpty().map {
            Transformation(
                id = it.id,
                name = it.name,
                image = it.image,
                ki = it.ki
            )
        }

        return CharacterDetail(
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

    fun toDomain(dto: PlanetDto): Planet {
        return Planet(
            id = dto.id,
            name = dto.name,
            isDestroyed = dto.isDestroyed,
            description = dto.description,
            image = dto.image
        )
    }

    fun toDomain(entity: PlanetEntity): Planet {
        return Planet(
            id = entity.id,
            name = entity.name,
            isDestroyed = entity.isDestroyed,
            description = entity.description,
            image = entity.image
        )
    }

    // --- ActionFigure & WorkoutLog <-> Room Entities ---

    fun toDomain(entity: FigureEntity): ActionFigure {
        return ActionFigure(
            id = entity.id,
            characterId = entity.characterId,
            name = entity.name,
            race = entity.race,
            image = entity.image,
            status = FigureStatus.fromLabel(entity.status),
            rating = entity.rating,
            customNote = entity.customNote,
            addedDate = entity.addedDate
        )
    }

    fun toEntity(domain: ActionFigure): FigureEntity {
        return FigureEntity(
            id = domain.id,
            characterId = domain.characterId,
            name = domain.name,
            race = domain.race,
            image = domain.image,
            status = domain.status.label,
            rating = domain.rating,
            customNote = domain.customNote,
            addedDate = domain.addedDate
        )
    }

    fun toDomain(entity: WorkoutLogEntity): WorkoutLog {
        return WorkoutLog(
            id = entity.id,
            exerciseName = entity.exerciseName,
            sets = entity.sets,
            reps = entity.reps,
            weightKg = entity.weightKg,
            kiGained = entity.kiGained,
            date = entity.date,
            notes = entity.notes
        )
    }

    fun toEntity(domain: WorkoutLog): WorkoutLogEntity {
        return WorkoutLogEntity(
            id = domain.id,
            exerciseName = domain.exerciseName,
            sets = domain.sets,
            reps = domain.reps,
            weightKg = domain.weightKg,
            kiGained = domain.kiGained,
            date = domain.date,
            notes = domain.notes
        )
    }
}
