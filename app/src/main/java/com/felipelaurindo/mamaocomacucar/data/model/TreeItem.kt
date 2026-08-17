package com.felipelaurindo.mamaocomacucar.data.model

/**
 * Represents a fruit tree mapped by the community.
 * Mirrors the web app's TreeItem interface.
 */
data class TreeItem(
    val id: String = "",
    val createdBy: String = "",
    val createdByName: String = "",
    val species: String = "",
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val currentStatus: TreeStatus = TreeStatus.VAZIO,
    val mainImage: String = "",
    val createdAt: String = "" // ISO String
)

/**
 * Ripening status of a fruit tree.
 */
enum class TreeStatus(val value: String, val emoji: String, val label: String) {
    FLORINDO("florindo", "🌸", "Florindo"),
    CRESCENDO("crescendo", "🍏", "Verde"),
    PRONTO("pronto", "🍎", "Maduro"),
    VAZIO("vazio", "🌳", "Vazio");

    companion object {
        fun fromValue(value: String): TreeStatus {
            return entries.find { it.value == value } ?: VAZIO
        }
    }
}
