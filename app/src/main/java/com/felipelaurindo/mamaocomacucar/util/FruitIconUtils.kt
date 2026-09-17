package com.felipelaurindo.mamaocomacucar.util

import androidx.annotation.DrawableRes
import com.felipelaurindo.mamaocomacucar.R
import com.felipelaurindo.mamaocomacucar.data.getFruitId
import java.text.Normalizer

private fun normalizeFruitName(species: String): String {
    val noAccents = Normalizer.normalize(species, Normalizer.Form.NFD)
        .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    return noAccents.lowercase().trim().replace("-", "_").replace(" ", "_").replace("/", "_")
}

/**
 * Returns the Android VectorDrawable resource ID corresponding to a fruit species name.
 * Every species in ALLOWED_FRUITS has its own dedicated, faithful vector drawable.
 */
@DrawableRes
fun getFruitDrawableRes(species: String): Int {
    val id = getFruitId(species)
    val norm = normalizeFruitName(species)
    return when {
        id == "abacate" || norm.contains("abacate") -> R.drawable.ic_fruit_abacate
        id == "abacaxi" || norm.contains("abacaxi") -> R.drawable.ic_fruit_abacaxi
        id == "acai" || norm.contains("acai") -> R.drawable.ic_fruit_acai
        id == "acerola" || norm.contains("acerola") -> R.drawable.ic_fruit_acerola
        id == "ameixa_da_mata" || norm.contains("ameixa_da_mata") || norm.contains("ameixa_mata") -> R.drawable.ic_fruit_ameixa_mata
        id == "ameixa_amarela" || norm.contains("ameixa_amarela") || norm.contains("nespera") -> R.drawable.ic_fruit_ameixa_amarela
        id == "amora" || norm.contains("amora") -> R.drawable.ic_fruit_amora
        id == "araca" || norm.contains("araca") -> R.drawable.ic_fruit_araca
        id == "araticum" || norm.contains("araticum") -> R.drawable.ic_fruit_araticum
        id == "atemoia" || norm.contains("atemoia") -> R.drawable.ic_fruit_atemoia
        id == "bacuri" || norm.contains("bacuri") -> R.drawable.ic_fruit_bacuri
        id == "banana" || norm.contains("banana") -> R.drawable.ic_fruit_banana
        id == "biriba" || norm.contains("biriba") -> R.drawable.ic_fruit_biriba
        id == "buriti" || norm.contains("buriti") -> R.drawable.ic_fruit_buriti
        id == "cacau" || norm.contains("cacau") -> R.drawable.ic_fruit_cacau
        id == "cagaita" || norm.contains("cagaita") -> R.drawable.ic_fruit_cagaita
        id == "cajaiba" || norm.contains("cajaiba") -> R.drawable.ic_fruit_cajaiba
        id == "caja" || norm.contains("caja") || norm.contains("tapereba") -> R.drawable.ic_fruit_caja
        id == "caju" || norm.contains("caju") -> R.drawable.ic_fruit_caju
        id == "camu_camu" || norm.contains("camu") -> R.drawable.ic_fruit_camu_camu
        id == "cambuci" || norm.contains("cambuci") -> R.drawable.ic_fruit_cambuci
        id == "carambola" || norm.contains("carambola") -> R.drawable.ic_fruit_carambola
        id == "carnauba" || norm.contains("carnauba") -> R.drawable.ic_fruit_carnauba
        id == "cherimoia" || norm.contains("cherimoia") -> R.drawable.ic_fruit_cherimoia
        id == "ciriguela" || norm.contains("ciriguela") || norm.contains("seriguela") -> R.drawable.ic_fruit_ciriguela
        id == "coco" || norm.contains("coco") -> R.drawable.ic_fruit_coco
        id == "cupuacu" || norm.contains("cupuacu") -> R.drawable.ic_fruit_cupuacu
        id == "figo" || norm.contains("figo") -> R.drawable.ic_fruit_figo
        id == "fruta_pao" || norm.contains("fruta_pao") -> R.drawable.ic_fruit_fruta_pao
        id == "fruta_do_conde" || norm.contains("fruta_do_conde") || norm.contains("fruta_conde") || norm.contains("pinha") -> R.drawable.ic_fruit_fruta_conde
        id == "gabiroba" || norm.contains("gabiroba") -> R.drawable.ic_fruit_gabiroba
        id == "goiaba" || norm.contains("goiaba") -> R.drawable.ic_fruit_goiaba
        id == "graviola" || norm.contains("graviola") -> R.drawable.ic_fruit_graviola
        id == "grumixama" || norm.contains("grumixama") -> R.drawable.ic_fruit_grumixama
        id == "guarana" || norm.contains("guarana") -> R.drawable.ic_fruit_guarana
        id == "jabuticaba" || norm.contains("jabuticaba") -> R.drawable.ic_fruit_jabuticaba
        id == "jaca" || norm.contains("jaca") -> R.drawable.ic_fruit_jaca
        id == "jambolao" || norm.contains("jambolao") -> R.drawable.ic_fruit_jambolao
        id == "jambo" || norm.contains("jambo") -> R.drawable.ic_fruit_jambo
        id == "jandiroba" || norm.contains("jandiroba") -> R.drawable.ic_fruit_jandiroba
        id == "jaracatia" || norm.contains("jaracatia") -> R.drawable.ic_fruit_jaracatia
        id == "jatoba" || norm.contains("jatoba") -> R.drawable.ic_fruit_jatoba
        id == "jenipapo" || norm.contains("jenipapo") -> R.drawable.ic_fruit_jenipapo
        id == "jua" || norm.contains("jua") -> R.drawable.ic_fruit_jua
        id == "laranja" || norm.contains("laranja") -> R.drawable.ic_fruit_laranja
        id == "limao" || norm.contains("limao") -> R.drawable.ic_fruit_limao
        id == "maba" || norm.contains("maba") -> R.drawable.ic_fruit_maba
        id == "macauba" || norm.contains("macauba") -> R.drawable.ic_fruit_macauba
        id == "maca" || (norm.contains("maca") && !norm.contains("macauba")) -> R.drawable.ic_fruit_maca
        id == "mamao" || norm.contains("mamao") -> R.drawable.ic_fruit_mamao
        id == "mangaba" || norm.contains("mangaba") -> R.drawable.ic_fruit_mangaba
        id == "manga" || norm.contains("manga") -> R.drawable.ic_fruit_manga
        id == "maracuja" || norm.contains("maracuja") -> R.drawable.ic_fruit_maracuja
        id == "melancia" || norm.contains("melancia") -> R.drawable.ic_fruit_melancia
        id == "melao" || norm.contains("melao") -> R.drawable.ic_fruit_melao
        id == "mexerica" || norm.contains("mexerica") -> R.drawable.ic_fruit_mexerica
        id == "morango" || norm.contains("morango") -> R.drawable.ic_fruit_morango
        id == "murici" || norm.contains("murici") -> R.drawable.ic_fruit_murici
        id == "pequi" || norm.contains("pequi") -> R.drawable.ic_fruit_pequi
        id == "pessego" || norm.contains("pessego") -> R.drawable.ic_fruit_pessego
        id == "pitaia" || norm.contains("pitaia") -> R.drawable.ic_fruit_pitaia
        id == "pitanga" || norm.contains("pitanga") -> R.drawable.ic_fruit_pitanga
        id == "pupunha" || norm.contains("pupunha") -> R.drawable.ic_fruit_pupunha
        id == "roma" || norm.contains("roma") -> R.drawable.ic_fruit_roma
        id == "sapota" || norm.contains("sapota") -> R.drawable.ic_fruit_sapota
        id == "sapoti" || norm.contains("sapoti") -> R.drawable.ic_fruit_sapoti
        id == "tamarindo" || norm.contains("tamarindo") -> R.drawable.ic_fruit_tamarindo
        id == "tucuma" || norm.contains("tucuma") -> R.drawable.ic_fruit_tucuma
        id == "uvaia" || norm.contains("uvaia") -> R.drawable.ic_fruit_uvaia
        id == "uva" || norm.contains("uva") -> R.drawable.ic_fruit_uva
        id == "umbu" || norm.contains("umbu") -> R.drawable.ic_fruit_umbu
        else -> R.drawable.ic_fruit_mamao
    }
}
