package com.openclassrooms.hexagonal.games.data.service

import com.openclassrooms.hexagonal.games.domain.model.Comment
import kotlinx.coroutines.flow.Flow


interface CommentApi {

    fun getComments(postId: String): Flow<List<Comment>>

    suspend fun addComment(postId: String, comment: Comment)
}
