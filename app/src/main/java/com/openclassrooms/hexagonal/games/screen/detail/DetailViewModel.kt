package com.openclassrooms.hexagonal.games.screen.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.hexagonal.games.data.repository.CommentRepository
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DetailViewModel @Inject constructor(private val postRepository: PostRepository, private val commentRepository: CommentRepository) : ViewModel() {

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> get() = _post

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> get() = _comments

    //fonction de recuperation du post par ID et mettre à jour le StateFlow
    fun getPost(postId: String) {
        viewModelScope.launch {
            postRepository.posts.collect { posts ->
                _post.value = posts.find { it.id == postId }
            }
        }
    }

    //load comments
    fun loadComments(postId: String) {
        viewModelScope.launch {
            commentRepository.getComments(postId).collect { commentList ->
                _comments.value = commentList
            }
        }
    }
}