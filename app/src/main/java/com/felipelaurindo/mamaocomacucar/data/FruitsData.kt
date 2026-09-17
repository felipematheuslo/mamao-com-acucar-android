package com.felipelaurindo.mamaocomacucar.data

import java.text.Normalizer

/**
 * Represents a canonical fruit definition with an explicit ID,
 * a display name (which displays both names in case of regional duplicates/synonyms),
 * and an optional list of aliases.
 */
data class FruitDefinition(
    val id: String,
    val displayName: String,
    val aliases: List<String> = emptyList()
)

val FRUIT_DEFINITIONS: List<FruitDefinition> = listOf(
    FruitDefinition("abacate", "Abacate"),
    FruitDefinition("abacaxi", "Abacaxi"),
    FruitDefinition("acai", "Açaí"),
    FruitDefinition("acerola", "Acerola"),
    FruitDefinition("ameixa_da_mata", "Ameixa-da-mata"),
    FruitDefinition("ameixa_amarela", "Ameixa-amarela / Nêspera", listOf("Ameixa-amarela", "Nêspera")),
    FruitDefinition("amora", "Amora"),
    FruitDefinition("araca", "Araçá"),
    FruitDefinition("araticum", "Araticum"),
    FruitDefinition("atemoia", "Atemoia"),
    FruitDefinition("bacuri", "Bacuri"),
    FruitDefinition("banana", "Banana"),
    FruitDefinition("biriba", "Biribá"),
    FruitDefinition("buriti", "Buriti"),
    FruitDefinition("cacau", "Cacau"),
    FruitDefinition("cagaita", "Cagaita"),
    FruitDefinition("cajaiba", "Cajaíba"),
    FruitDefinition("caja", "Cajá / Taperebá", listOf("Cajá", "Taperebá")),
    FruitDefinition("caju", "Caju"),
    FruitDefinition("camu_camu", "Camu-camu"),
    FruitDefinition("cambuci", "Cambuci"),
    FruitDefinition("carambola", "Carambola"),
    FruitDefinition("carnauba", "Carnaúba"),
    FruitDefinition("cherimoia", "Cherimoia"),
    FruitDefinition("ciriguela", "Ciriguela / Seriguela", listOf("Ciriguela", "Seriguela")),
    FruitDefinition("coco", "Coco"),
    FruitDefinition("cupuacu", "Cupuaçu"),
    FruitDefinition("figo", "Figo"),
    FruitDefinition("fruta_pao", "Fruta-pão"),
    FruitDefinition("fruta_do_conde", "Fruta-do-conde / Pinha", listOf("Fruta-do-conde", "Pinha", "Ata")),
    FruitDefinition("gabiroba", "Gabiroba"),
    FruitDefinition("goiaba", "Goiaba"),
    FruitDefinition("graviola", "Graviola"),
    FruitDefinition("grumixama", "Grumixama"),
    FruitDefinition("guarana", "Guaraná"),
    FruitDefinition("jabuticaba", "Jabuticaba"),
    FruitDefinition("jaca", "Jaca"),
    FruitDefinition("jambo", "Jambo"),
    FruitDefinition("jambolao", "Jambolão"),
    FruitDefinition("jandiroba", "Jandiroba"),
    FruitDefinition("jaracatia", "Jaracatiá"),
    FruitDefinition("jatoba", "Jatobá"),
    FruitDefinition("jenipapo", "Jenipapo"),
    FruitDefinition("jua", "Juá"),
    FruitDefinition("laranja", "Laranja"),
    FruitDefinition("limao", "Limão"),
    FruitDefinition("maba", "Maba"),
    FruitDefinition("maca", "Maçã"),
    FruitDefinition("macauba", "Macaúba"),
    FruitDefinition("mamao", "Mamão"),
    FruitDefinition("manga", "Manga"),
    FruitDefinition("mangaba", "Mangaba"),
    FruitDefinition("maracuja", "Maracujá"),
    FruitDefinition("melancia", "Melancia"),
    FruitDefinition("melao", "Melão"),
    FruitDefinition("mexerica", "Mexerica"),
    FruitDefinition("morango", "Morango"),
    FruitDefinition("murici", "Murici"),
    FruitDefinition("pequi", "Pequi"),
    FruitDefinition("pessego", "Pêssego"),
    FruitDefinition("pitaia", "Pitaia"),
    FruitDefinition("pitanga", "Pitanga"),
    FruitDefinition("pupunha", "Pupunha"),
    FruitDefinition("roma", "Romã"),
    FruitDefinition("sapota", "Sapota"),
    FruitDefinition("sapoti", "Sapoti"),
    FruitDefinition("tamarindo", "Tamarindo"),
    FruitDefinition("tucuma", "Tucumã"),
    FruitDefinition("umbu", "Umbu"),
    FruitDefinition("uva", "Uva"),
    FruitDefinition("uvaia", "Uvaia")
)

/**
 * Official list of allowed Brazilian fruit species display names.
 * Mirrors the web app's fruits.json, with regional duplicates merged into unified display names.
 */
val ALLOWED_FRUITS: List<String> = FRUIT_DEFINITIONS.map { it.displayName }

private fun normalizeFruitKey(input: String): String {
    val noAccents = Normalizer.normalize(input, Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    return noAccents.lowercase().trim()
        .replace("-", "_")
        .replace(" ", "_")
        .replace("/", "_")
}

/**
 * Resolves any alias, legacy species name, or ID to its canonical FruitDefinition.
 */
fun findFruitDefinition(speciesOrAlias: String): FruitDefinition? {
    val clean = speciesOrAlias.trim()
    if (clean.isBlank()) return null
    val norm = normalizeFruitKey(clean)

    return FRUIT_DEFINITIONS.find { def ->
        def.id == norm ||
        normalizeFruitKey(def.displayName) == norm ||
        def.aliases.any { normalizeFruitKey(it) == norm } ||
        norm.contains(def.id) ||
        (def.aliases.isNotEmpty() && def.aliases.any { norm.contains(normalizeFruitKey(it)) })
    }
}

/**
 * Returns the unified display name for any species name (including legacy names like 'Pinha' or 'Seriguela').
 */
fun getFruitDisplayName(species: String): String {
    return findFruitDefinition(species)?.displayName ?: species
}

/**
 * Returns the canonical fruit ID for a given species name.
 */
fun getFruitId(species: String): String {
    return findFruitDefinition(species)?.id ?: normalizeFruitKey(species)
}
