package com.openclassrooms.hexagonal.games.screen.ad

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.hexagonal.games.data.repository.PostRepository
import com.openclassrooms.hexagonal.games.domain.model.Post
import com.openclassrooms.hexagonal.games.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.UUID
import javax.inject.Inject

/**
 * This ViewModel manages data and interactions related to adding new posts in the AddScreen.
 * It utilizes dependency injection to retrieve a PostRepository instance for interacting with post data.
 */
@HiltViewModel
class AddViewModel @Inject constructor(private val postRepository: PostRepository) : ViewModel() {

  /**
   * Internal mutable state flow representing the current post being edited.
   */
  private var _post = MutableStateFlow(
    Post(
      id = UUID.randomUUID().toString(),
      title = "",
      description = "",
      photoUrl = null,
      timestamp = System.currentTimeMillis(),
      author = null
    )
  )

  /**
   * Public state flow representing the current post being edited.
   * This is immutable for consumers.
   */
  val post: StateFlow<Post>
    get() = _post

  /**
   * StateFlow derived from the post that emits a FormError if the title is empty, null otherwise.
   */
  val error = post.map {
    verifyPost()
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000),
    initialValue = null
  )

  /**
   * Handles form events like title and description changes.
   *
   * @param formEvent The form event to be processed.
   */
  fun onAction(formEvent: FormEvent) {
    when (formEvent) {
      is FormEvent.DescriptionChanged -> {
        _post.value = _post.value.copy(
          description = formEvent.description
        )
      }
      //gestion de la maj de l'url de la photo
      is FormEvent.PhotoUrlChanged -> {
        _post.value = _post.value.copy(
          photoUrl = formEvent.photoUrl
        )
      }
      is FormEvent.TitleChanged -> {
        _post.value = _post.value.copy(
          title = formEvent.title
        )
      }
    }
  }

  /**
   * Attempts to add the current post to the repository after setting the author.
   *
   * TODO: Implement logic to retrieve the current user.
   */
  fun addPost() {
    val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseUser = firebaseAuth.currentUser

    if (firebaseUser != null) {

      val currentUser = User(
        id = firebaseUser.uid,
        firstname = firebaseUser.displayName ?: "inconnu",
        lastname = ""
      )

      //new post with randomID and current user
      val newPost = _post.value.copy(
        id = UUID.randomUUID().toString(),
        author = currentUser,
        uid = firebaseUser.uid,
        timestamp = System.currentTimeMillis() //horodatage
      )

      //appel de la fonction addPost du repository
      postRepository.addPost(newPost)
    } else {
      //user non connecté
      Log.e("AddViewModel", "Utilisateur non connecté - Impossible d'ajouter le post.")
    }
  }

  /**
   * Verifies mandatory fields of the post
   * and returns a corresponding FormError if so.
   *
   * @return A FormError.TitleError if title is empty, null otherwise.
   */
  fun verifyPost(): FormError? {
    val title = _post.value.title
    return when {
      title.isBlank() -> FormError.TitleError
      title.length < 3 -> FormError.TitleError

      else -> null
    }
  }

}
