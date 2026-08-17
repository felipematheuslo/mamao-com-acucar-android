package com.felipelaurindo.mamaocomacucar.util

import java.text.Normalizer

/**
 * Normalizes a username following the same rules as the web app:
 * - Remove accents
 * - Lowercase
 * - Remove spaces
 * - Only allow letters, numbers, dot, hyphen, and underscore
 */
fun normalizeUsername(value: String): String {
    return Normalizer.normalize(value, Normalizer.Form.NFD)
        .replace(Regex("[\\u0300-\\u036f]"), "") // Remove accents
        .lowercase()
        .replace(Regex("\\s+"), "") // Remove spaces
        .replace(Regex("[^a-z0-9_.-]"), "") // Only allow safe chars
}

/**
 * Normalizes a string for accent-insensitive search comparison.
 */
fun normalizeString(str: String): String {
    return Normalizer.normalize(str, Normalizer.Form.NFD)
        .replace(Regex("[\\u0300-\\u036f]"), "")
        .replace(Regex("[\\s-]"), "")
        .lowercase()
}
