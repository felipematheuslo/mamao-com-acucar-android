package com.felipelaurindo.mamaocomacucar.data.repository

import com.felipelaurindo.mamaocomacucar.data.model.CommentUpdate
import com.felipelaurindo.mamaocomacucar.data.model.TreeItem
import com.felipelaurindo.mamaocomacucar.data.model.TreeStatus
import com.felipelaurindo.mamaocomacucar.data.model.UserProfile
import com.felipelaurindo.mamaocomacucar.util.normalizeUsername
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Date

/**
 * Repository for all Firestore operations.
 * Mirrors the web app's DatabaseAdapter class exactly.
 */
class FirestoreRepository {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // --- TREES ---

    suspend fun getTrees(): List<TreeItem> {
        return try {
            val snapshot = db.collection("trees").get().await()
            snapshot.documents.map { doc ->
                documentToTreeItem(doc.id, doc.data ?: emptyMap())
            }
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepo", "Error getting trees", e)
            emptyList()
        }
    }

    fun subscribeTrees(callback: (List<TreeItem>) -> Unit): ListenerRegistration {
        return db.collection("trees").addSnapshotListener { snapshot, error ->
            if (error != null) {
                android.util.Log.e("FirestoreRepo", "Error subscribing trees", error)
                return@addSnapshotListener
            }
            val trees = snapshot?.documents?.map { doc ->
                documentToTreeItem(doc.id, doc.data ?: emptyMap())
            } ?: emptyList()
            callback(trees)
        }
    }

    suspend fun addTree(tree: TreeItem): TreeItem {
        val id = "tree-${System.currentTimeMillis()}"
        val data = treeItemToMap(tree)
        try {
            db.collection("trees").document(id).set(data).await()
            return tree.copy(id = id)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepo", "Error adding tree", e)
            throw e
        }
    }

    suspend fun updateTreeStatus(treeId: String, status: TreeStatus) {
        try {
            db.collection("trees").document(treeId)
                .update("currentStatus", status.value).await()
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepo", "Error updating tree status", e)
        }
    }

    // --- UPDATES ---

    fun subscribeUpdates(treeId: String, callback: (List<CommentUpdate>) -> Unit): ListenerRegistration {
        return db.collection("updates")
            .whereEqualTo("treeId", treeId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("FirestoreRepo", "Error subscribing updates", error)
                    return@addSnapshotListener
                }
                val updates = snapshot?.documents?.map { doc ->
                    documentToCommentUpdate(doc.id, doc.data ?: emptyMap())
                }?.sortedByDescending { it.createdAt } ?: emptyList()
                callback(updates)
            }
    }

    suspend fun addUpdate(update: CommentUpdate): CommentUpdate {
        val id = "up-${System.currentTimeMillis()}"
        val data = commentUpdateToMap(update)
        try {
            db.collection("updates").document(id).set(data).await()
            // Also update tree status synchronously
            updateTreeStatus(update.treeId, update.statusAtReport)
            return update.copy(id = id)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepo", "Error adding update", e)
            throw e
        }
    }

    // --- USER PROFILE & UNIQUE USERNAMES ---

    suspend fun getUserProfile(uid: String): UserProfile? {
        return try {
            val doc = db.collection("users").document(uid).get().await()
            if (doc.exists()) {
                UserProfile(
                    id = doc.id,
                    email = doc.getString("email") ?: "",
                    displayName = doc.getString("displayName") ?: "",
                    username = doc.getString("username") ?: "",
                    createdAt = doc.getString("createdAt") ?: ""
                )
            } else null
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepo", "Error getting user profile", e)
            null
        }
    }

    suspend fun getEmailByUsername(usernameOrEmail: String): String? {
        val trimmed = usernameOrEmail.trim()
        if (trimmed.isEmpty()) return null
        if (trimmed.contains("@")) return trimmed

        val normalized = normalizeUsername(trimmed)
        val lower = trimmed.lowercase()
        val withoutAt = if (trimmed.startsWith("@")) trimmed.drop(1) else trimmed
        val withoutAtNormalized = normalizeUsername(withoutAt)

        val keysToCheck = listOfNotNull(
            normalized.ifEmpty { null },
            withoutAtNormalized.ifEmpty { null },
            lower.ifEmpty { null },
            withoutAt.lowercase().ifEmpty { null }
        ).distinct()

        for (key in keysToCheck) {
            try {
                val usernameDoc = db.collection("usernames").document(key).get().await()
                if (usernameDoc.exists()) {
                    val directEmail = usernameDoc.getString("email")
                    if (!directEmail.isNullOrBlank()) return directEmail

                    val uid = usernameDoc.getString("uid")
                    if (!uid.isNullOrBlank()) {
                        val userDoc = db.collection("users").document(uid).get().await()
                        val userEmail = userDoc.getString("email")
                        if (!userEmail.isNullOrBlank()) return userEmail
                    }
                }
            } catch (e: Exception) {
                android.util.Log.w("FirestoreRepo", "Error checking usernames doc for key: $key", e)
            }
        }

        val queriesToTry = listOfNotNull(
            normalized.ifEmpty { null },
            withoutAtNormalized.ifEmpty { null },
            trimmed.ifEmpty { null },
            lower.ifEmpty { null },
            withoutAt.ifEmpty { null },
            "@$normalized",
            "@$withoutAtNormalized"
        ).distinct()

        for (queryVal in queriesToTry) {
            try {
                val snap = db.collection("users")
                    .whereEqualTo("username", queryVal)
                    .limit(1)
                    .get().await()
                if (!snap.isEmpty) {
                    val email = snap.documents[0].getString("email")
                    if (!email.isNullOrBlank()) return email
                }
            } catch (e: Exception) {
                android.util.Log.w("FirestoreRepo", "Error querying users for username: $queryVal", e)
            }
        }

        return null
    }

    suspend fun checkUsernameUnique(username: String): Boolean {
        val normalized = normalizeUsername(username)
        return try {
            val doc = db.collection("usernames").document(normalized).get().await()
            !doc.exists()
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepo", "Error checking username", e)
            false
        }
    }

    suspend fun registerUsername(uid: String, username: String, email: String, displayName: String) {
        val normalized = normalizeUsername(username)
        try {
            db.collection("usernames").document(normalized)
                .set(mapOf("uid" to uid, "email" to email, "username" to normalized)).await()
            db.collection("users").document(uid).set(
                mapOf(
                    "id" to uid,
                    "email" to email,
                    "displayName" to displayName,
                    "username" to normalized,
                    "createdAt" to java.time.Instant.now().toString()
                )
            ).await()
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepo", "Error registering username", e)
            throw e
        }
    }

    suspend fun updateUsername(
        uid: String,
        oldUsername: String,
        newUsername: String,
        email: String,
        displayName: String
    ) {
        val normalizedOld = normalizeUsername(oldUsername)
        val normalizedNew = normalizeUsername(newUsername)
        if (normalizedOld == normalizedNew) return

        try {
            // Get existing createdAt to preserve it
            val userDoc = db.collection("users").document(uid).get().await()
            val createdAt = userDoc.getString("createdAt") ?: java.time.Instant.now().toString()

            // Create new username document
            db.collection("usernames").document(normalizedNew)
                .set(mapOf("uid" to uid, "email" to email, "username" to normalizedNew)).await()

            // Update user profile
            db.collection("users").document(uid).set(
                mapOf(
                    "id" to uid,
                    "email" to email,
                    "displayName" to displayName,
                    "username" to normalizedNew,
                    "createdAt" to createdAt
                )
            ).await()

            // Delete old username
            if (normalizedOld.isNotEmpty()) {
                db.collection("usernames").document(normalizedOld).delete().await()
            }
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepo", "Error updating username", e)
            throw e
        }
    }

    suspend fun deleteUserProfile(uid: String, username: String) {
        val normalized = normalizeUsername(username)
        try {
            // 1. Delete user profile
            db.collection("users").document(uid).delete().await()

            // 2. Delete username mapping
            if (normalized.isNotEmpty()) {
                db.collection("usernames").document(normalized).delete().await()
            }

            // 3. Delete trees created by this user
            val treesSnapshot = db.collection("trees")
                .whereEqualTo("createdBy", uid).get().await()
            for (doc in treesSnapshot.documents) {
                db.collection("trees").document(doc.id).delete().await()
            }

            // 4. Delete updates created by this user
            val updatesSnapshot = db.collection("updates")
                .whereEqualTo("createdBy", uid).get().await()
            for (doc in updatesSnapshot.documents) {
                db.collection("updates").document(doc.id).delete().await()
            }
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepo", "Error deleting user profile", e)
            throw e
        }
    }

    // --- MAPPING HELPERS ---

    private fun documentToTreeItem(id: String, data: Map<String, Any>): TreeItem {
        return TreeItem(
            id = id,
            createdBy = data["createdBy"] as? String ?: "",
            createdByName = data["createdByName"] as? String ?: "",
            species = data["species"] as? String ?: "",
            name = data["name"] as? String ?: "",
            latitude = (data["latitude"] as? Number)?.toDouble() ?: 0.0,
            longitude = (data["longitude"] as? Number)?.toDouble() ?: 0.0,
            currentStatus = TreeStatus.fromValue(data["currentStatus"] as? String ?: "vazio"),
            mainImage = data["mainImage"] as? String ?: "",
            createdAt = data["createdAt"] as? String ?: ""
        )
    }

    private fun treeItemToMap(tree: TreeItem): Map<String, Any> {
        val map = mutableMapOf<String, Any>(
            "createdBy" to tree.createdBy,
            "createdByName" to tree.createdByName,
            "species" to tree.species,
            "name" to tree.name,
            "latitude" to tree.latitude,
            "longitude" to tree.longitude,
            "currentStatus" to tree.currentStatus.value,
            "createdAt" to tree.createdAt
        )
        if (tree.mainImage.isNotEmpty()) {
            map["mainImage"] = tree.mainImage
        }
        return map
    }

    private fun documentToCommentUpdate(id: String, data: Map<String, Any>): CommentUpdate {
        return CommentUpdate(
            id = id,
            treeId = data["treeId"] as? String ?: "",
            createdBy = data["createdBy"] as? String ?: "",
            createdByName = data["createdByName"] as? String ?: "",
            comment = data["comment"] as? String ?: "",
            statusAtReport = TreeStatus.fromValue(data["statusAtReport"] as? String ?: "vazio"),
            createdAt = data["createdAt"] as? String ?: ""
        )
    }

    private fun commentUpdateToMap(update: CommentUpdate): Map<String, Any> {
        return mapOf(
            "treeId" to update.treeId,
            "createdBy" to update.createdBy,
            "createdByName" to update.createdByName,
            "comment" to update.comment,
            "statusAtReport" to update.statusAtReport.value,
            "createdAt" to update.createdAt
        )
    }
}
