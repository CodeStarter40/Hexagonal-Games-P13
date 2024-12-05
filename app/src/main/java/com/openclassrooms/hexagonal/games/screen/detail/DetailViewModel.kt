package com.openclassrooms.hexagonal.games.screen.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.hexagonal.games.domain.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DetailViewModel @Inject constructor():ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> get() = _post

    //fonction de recuperation du post par ID et mettre à jour le StateFlow
    fun getPost(postId: String) {
        viewModelScope.launch {
            firestore.collection("posts").document(postId).get()
                .addOnSuccessListener { document ->
                    val fetchedPost = document.toObject(Post::class.java)
                    _post.value = fetchedPost
                    Log.d("DetailViewModel", "Post récupéré avec succès: $fetchedPost")
                }
                .addOnFailureListener { exception ->
                    Log.e("DetailViewModel", "Erreur lors de la récupération du post", exception)
                    _post.value = null
                }
        }
    }
}