package com.felipelaurindo.mamaocomacucar.data.model

/**
 * Represents a user's profile in the app.
 * Mirrors the web app's UserProfile interface.
 */
data class UserProfile(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val username: String = "",
    val createdAt: String = "" // ISO String
)

/**
 * Represents the currently logged-in user (in-memory state).
 */
data class LoggedUser(
    val uid: String,
    val displayName: String,
    val username: String,
    val email: String
)
