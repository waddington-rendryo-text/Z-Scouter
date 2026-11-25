package com.example.domain.usecase

import com.example.domain.model.Character

class FilterCharactersUseCase {
    operator fun invoke(
        characters: List<Character>,
        query: String,
        raceFilter: String,
        affiliationFilter: String
    ): List<Character> {
        return characters.filter { char ->
            val matchesQuery = query.isBlank() ||
                char.name.contains(query, ignoreCase = true) ||
                (char.description?.contains(query, ignoreCase = true) == true)

            val matchesRace = if (raceFilter == "Todos") true else {
                when (raceFilter) {
                    "Saiyajin" -> char.race.equals("Saiyan", ignoreCase = true)
                    "Humano" -> char.race.equals("Human", ignoreCase = true)
                    "Namekuseijin" -> char.race.equals("Namekian", ignoreCase = true)
                    "Androide" -> char.race.equals("Android", ignoreCase = true)
                    "Majin" -> char.race.equals("Majin", ignoreCase = true)
                    "Frieza Race" -> char.race.equals("Frieza Race", ignoreCase = true)
                    "Deuses / Anjos" -> char.race.equals("God", ignoreCase = true) || char.race.equals("Angel", ignoreCase = true)
                    else -> true
                }
            }

            val matchesAffiliation = if (affiliationFilter == "Todos") true else {
                when (affiliationFilter) {
                    "Z Fighter" -> char.affiliation.equals("Z Fighter", ignoreCase = true)
                    "Exército de Freeza" -> char.affiliation.equals("Army of Frieza", ignoreCase = true) || char.affiliation.equals("Freelancer", ignoreCase = true)
                    "Red Ribbon" -> char.affiliation.equals("Red Ribbon Army", ignoreCase = true)
                    "Outros" -> !char.affiliation.equals("Z Fighter", ignoreCase = true)
                    else -> true
                }
            }

            matchesQuery && matchesRace && matchesAffiliation
        }
    }
}
