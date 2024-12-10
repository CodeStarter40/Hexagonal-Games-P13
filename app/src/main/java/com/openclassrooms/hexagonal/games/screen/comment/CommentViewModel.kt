package com.openclassrooms.hexagonal.games.screen.comment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.data.repository.CommentRepository
import com.openclassrooms.hexagonal.games.domain.model.Comment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CommentViewModel @Inject constructor(private val commentRepository: CommentRepository) : ViewModel() {

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> get() = _comments

    fun saveComment(postId: String, comment: Comment) {
        viewModelScope.launch {
        try {
            commentRepository.addComment(postId, comment)
        } catch (e: Exception) {
            Log.e("CommentViewModel", "Erreur lors de l'ajout du commentaire", e)
        }
    }
    }
}
