package com.felipelaurindo.mamaocomacucar.util

import androidx.annotation.DrawableRes
import com.felipelaurindo.mamaocomacucar.R

/**
 * Returns the Android VectorDrawable resource ID corresponding to a fruit species name.
 * Fallbacks to R.drawable.ic_fruit_default_fruta if not explicitly mapped.
 */
@DrawableRes
fun getFruitDrawableRes(species: String): Int {
    val normalized = species.lowercase().trim()
    return when {
        normalized.contains("mamão") || normalized.contains("mamao") -> R.drawable.ic_fruit_mamao
        normalized.contains("pitanga") -> R.drawable.ic_fruit_pitanga
        normalized.contains("amora") -> R.drawable.ic_fruit_amora
        normalized.contains("caju") || normalized.contains("cajá") || normalized.contains("ciriguela") || normalized.contains("seriguela") -> R.drawable.ic_fruit_caju
        normalized.contains("goiaba") || normalized.contains("araçá") || normalized.contains("cambuci") -> R.drawable.ic_fruit_goiaba
        normalized.contains("manga") -> R.drawable.ic_fruit_manga
        normalized.contains("jabuticaba") || normalized.contains("grumixama") || normalized.contains("jambolão") -> R.drawable.ic_fruit_jabuticaba
        normalized.contains("banana") -> R.drawable.ic_fruit_banana
        normalized.contains("abacaxi") -> R.drawable.ic_fruit_abacaxi
        normalized.contains("açaí") || normalized.contains("acai") || normalized.contains("carnaúba") -> R.drawable.ic_fruit_acai
        normalized.contains("maracujá") || normalized.contains("maracuja") -> R.drawable.ic_fruit_maracuja
        normalized.contains("coco") || normalized.contains("buriti") -> R.drawable.ic_fruit_coco
        normalized.contains("cacau") || normalized.contains("cupuaçu") || normalized.contains("tamarindo") -> R.drawable.ic_fruit_cacau
        else -> R.drawable.ic_fruit_default_fruta
    }
}

/**
 * Returns the asset path of the SVG icon for a fruit species.
 */
fun getFruitSvgPath(species: String): String {
    val normalized = species.lowercase().trim()
    val key = when {
        normalized.contains("mamão") || normalized.contains("mamao") -> "mamao"
        normalized.contains("pitanga") -> "pitanga"
        normalized.contains("amora") -> "amora"
        normalized.contains("caju") || normalized.contains("cajá") || normalized.contains("ciriguela") || normalized.contains("seriguela") -> "caju"
        normalized.contains("goiaba") || normalized.contains("araçá") || normalized.contains("cambuci") -> "goiaba"
        normalized.contains("manga") -> "manga"
        normalized.contains("jabuticaba") || normalized.contains("grumixama") || normalized.contains("jambolão") -> "jabuticaba"
        normalized.contains("banana") -> "banana"
        normalized.contains("abacaxi") -> "abacaxi"
        normalized.contains("açaí") || normalized.contains("acai") || normalized.contains("carnaúba") -> "acai"
        normalized.contains("maracujá") || normalized.contains("maracuja") -> "maracuja"
        normalized.contains("coco") || normalized.contains("buriti") -> "coco"
        normalized.contains("cacau") || normalized.contains("cupuaçu") || normalized.contains("tamarindo") -> "cacau"
        else -> "default_fruta"
    }
    return "fruits_svg/ic_fruit_$key.svg"
}
