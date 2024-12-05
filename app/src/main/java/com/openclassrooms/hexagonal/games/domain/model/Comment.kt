package com.openclassrooms.hexagonal.games.domain.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * This class represents a Comment data object. It holds information about a comment.
 * Cette clase represente un data object Comment. Elle contient les informations sur le commentaire
 */

data class Comment(
    val id: String = "",

    val authorName: String = "",

    val content: String = "",

    @ServerTimestamp val timestamp: Date? = null
)
