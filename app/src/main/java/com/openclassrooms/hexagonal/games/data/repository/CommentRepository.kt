package com.openclassrooms.hexagonal.games.data.repository

import com.openclassrooms.hexagonal.games.data.service.CommentApi
import com.openclassrooms.hexagonal.games.domain.model.Comment
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(private val commentApi: CommentApi) {

    fun getComments(postId: String): Flow<List<Comment>> {
        return commentApi.getComments(postId)
    }

    suspend fun addComment(postId: String, comment: Comment) {
        commentApi.addComment(postId,comment)
    }
}
