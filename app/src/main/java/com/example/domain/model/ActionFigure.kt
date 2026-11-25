package com.example.domain.model

enum class FigureStatus(val label: String) {
    EXPOSED("Exposto no Estúdio"),
    BOXED("Na Caixa Original"),
    WISHLIST("Wishlist (Desejo)");

    companion object {
        fun fromLabel(label: String): FigureStatus {
            return entries.firstOrNull { it.label.equals(label, ignoreCase = true) } ?: EXPOSED
        }
    }
}

data class ActionFigure(
    val id: Int = 0,
    val characterId: Int,
    val name: String,
    val race: String,
    val image: String,
    val status: FigureStatus = FigureStatus.EXPOSED,
    val rating: Int = 5,
    val customNote: String = "",
    val addedDate: Long = System.currentTimeMillis()
)
