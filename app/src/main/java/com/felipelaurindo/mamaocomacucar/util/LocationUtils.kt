package com.felipelaurindo.mamaocomacucar.util

import kotlin.math.*

/**
 * Calculates the distance between two geographic points using the Haversine formula.
 * @return Distance in kilometers.
 */
fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0 // Earth radius in km
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}

/**
 * Formats a distance value for display.
 * Shows meters if < 1km, otherwise km with 1 decimal.
 */
fun formatDistance(distanceKm: Double): String {
    return if (distanceKm >= 1.0) {
        "${String.format("%.1f", distanceKm)} km"
    } else {
        "${(distanceKm * 1000).toInt()} m"
    }
}
