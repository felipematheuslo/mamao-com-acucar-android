package com.felipelaurindo.mamaocomacucar.data.model

/**
 * Represents a community update/report on a fruit tree.
 * Mirrors the web app's CommentUpdate interface.
 */
data class CommentUpdate(
    val id: String = "",
    val treeId: String = "",
    val createdBy: String = "",
    val createdByName: String = "",
    val comment: String = "",
    val statusAtReport: TreeStatus = TreeStatus.VAZIO,
    val createdAt: String = "" // ISO String
)
