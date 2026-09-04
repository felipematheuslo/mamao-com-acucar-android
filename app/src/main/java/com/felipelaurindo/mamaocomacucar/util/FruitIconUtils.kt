package com.felipelaurindo.mamaocomacucar.util

import androidx.annotation.DrawableRes
import com.felipelaurindo.mamaocomacucar.R
import java.text.Normalizer

private fun normalizeFruitName(species: String): String {
    val noAccents = Normalizer.normalize(species, Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    return noAccents.lowercase().trim().replace("-", "_")
}

/**
 * Returns the Android VectorDrawable resource ID corresponding to a fruit species name.
 * Every species in ALLOWED_FRUITS has its own dedicated, faithful vector drawable.
 */
@DrawableRes
fun getFruitDrawableRes(species: String): Int {
    val norm = normalizeFruitName(species)
    return when {
        norm.contains("abacaxi") -> R.drawable.ic_fruit_abacaxi
        norm.contains("acai") -> R.drawable.ic_fruit_acai
        norm.contains("acerola") -> R.drawable.ic_fruit_acerola
        norm.contains("ameixa_da_mata") || norm.contains("ameixa_mata") -> R.drawable.ic_fruit_ameixa_mata
        norm.contains("ameixa_amarela") -> R.drawable.ic_fruit_ameixa_amarela
        norm.contains("amora") -> R.drawable.ic_fruit_amora
        norm.contains("araca") -> R.drawable.ic_fruit_araca
        norm.contains("araticum") -> R.drawable.ic_fruit_araticum
        norm.contains("atemoia") -> R.drawable.ic_fruit_atemoia
        norm.contains("bacuri") -> R.drawable.ic_fruit_bacuri
        norm.contains("banana") -> R.drawable.ic_fruit_banana
        norm.contains("biriba") -> R.drawable.ic_fruit_biriba
        norm.contains("buriti") -> R.drawable.ic_fruit_buriti
        norm.contains("cacau") -> R.drawable.ic_fruit_cacau
        norm.contains("cagaita") -> R.drawable.ic_fruit_cagaita
        norm.contains("cajaiba") -> R.drawable.ic_fruit_cajaiba
        norm.contains("caja") -> R.drawable.ic_fruit_caja
        norm.contains("caju") -> R.drawable.ic_fruit_caju
        norm.contains("camu") -> R.drawable.ic_fruit_camu_camu
        norm.contains("cambuci") -> R.drawable.ic_fruit_cambuci
        norm.contains("carambola") -> R.drawable.ic_fruit_carambola
        norm.contains("carnauba") -> R.drawable.ic_fruit_carnauba
        norm.contains("cherimoia") -> R.drawable.ic_fruit_cherimoia
        norm.contains("ciriguela") -> R.drawable.ic_fruit_ciriguela
        norm.contains("coco") -> R.drawable.ic_fruit_coco
        norm.contains("cupuacu") -> R.drawable.ic_fruit_cupuacu
        norm.contains("fruta_pao") -> R.drawable.ic_fruit_fruta_pao
        norm.contains("fruta_do_conde") || norm.contains("fruta_conde") -> R.drawable.ic_fruit_fruta_conde
        norm.contains("gabiroba") -> R.drawable.ic_fruit_gabiroba
        norm.contains("goiaba") -> R.drawable.ic_fruit_goiaba
        norm.contains("graviola") -> R.drawable.ic_fruit_graviola
        norm.contains("grumixama") -> R.drawable.ic_fruit_grumixama
        norm.contains("guarana") -> R.drawable.ic_fruit_guarana
        norm.contains("jabuticaba") -> R.drawable.ic_fruit_jabuticaba
        norm.contains("jaca") -> R.drawable.ic_fruit_jaca
        norm.contains("jambolao") -> R.drawable.ic_fruit_jambolao
        norm.contains("jambo") -> R.drawable.ic_fruit_jambo
        norm.contains("jandiroba") -> R.drawable.ic_fruit_jandiroba
        norm.contains("jaracatia") -> R.drawable.ic_fruit_jaracatia
        norm.contains("jatoba") -> R.drawable.ic_fruit_jatoba
        norm.contains("jenipapo") -> R.drawable.ic_fruit_jenipapo
        norm.contains("jua") -> R.drawable.ic_fruit_jua
        norm.contains("maba") -> R.drawable.ic_fruit_maba
        norm.contains("macauba") -> R.drawable.ic_fruit_macauba
        norm.contains("mamao") -> R.drawable.ic_fruit_mamao
        norm.contains("mangaba") -> R.drawable.ic_fruit_mangaba
        norm.contains("manga") -> R.drawable.ic_fruit_manga
        norm.contains("maracuja") -> R.drawable.ic_fruit_maracuja
        norm.contains("melancia") -> R.drawable.ic_fruit_melancia
        norm.contains("melao") -> R.drawable.ic_fruit_melao
        norm.contains("mexerica") -> R.drawable.ic_fruit_mexerica
        norm.contains("morango") -> R.drawable.ic_fruit_morango
        norm.contains("murici") -> R.drawable.ic_fruit_murici
        norm.contains("nespera") -> R.drawable.ic_fruit_nespera
        norm.contains("pequi") -> R.drawable.ic_fruit_pequi
        norm.contains("pinha") -> R.drawable.ic_fruit_pinha
        norm.contains("pitaia") -> R.drawable.ic_fruit_pitaia
        norm.contains("pitanga") -> R.drawable.ic_fruit_pitanga
        norm.contains("pupunha") -> R.drawable.ic_fruit_pupunha
        norm.contains("sapota") -> R.drawable.ic_fruit_sapota
        norm.contains("sapoti") -> R.drawable.ic_fruit_sapoti
        norm.contains("seriguela") -> R.drawable.ic_fruit_seriguela
        norm.contains("tapereba") -> R.drawable.ic_fruit_tapereba
        norm.contains("tamarindo") -> R.drawable.ic_fruit_tamarindo
        norm.contains("tucuma") -> R.drawable.ic_fruit_tucuma
        norm.contains("uvaia") -> R.drawable.ic_fruit_uvaia
        norm.contains("uva") -> R.drawable.ic_fruit_uva
        norm.contains("umbu") -> R.drawable.ic_fruit_umbu
        else -> R.drawable.ic_fruit_mamao
    }
}

