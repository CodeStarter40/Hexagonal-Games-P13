package com.openclassrooms.hexagonal.games.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.openclassrooms.hexagonal.games.screen.login.LoginUiState
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume

//repository pour inclure les fun de gestion de compte comme la déconnexion et la suppression de compte via firebase

class UserRepository @Inject constructor(private val firebaseAuth: FirebaseAuth, private val libraryRepository: LibraryRepository) {

    //déconnexion du user actuel
    fun signOut() {
        firebaseAuth.signOut()
    }

    //suppression du compte actuel
    fun deleteAccount(onComplete: (Boolean, String?) -> Unit) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            currentUser.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //compte supprimé avec succès
                onComplete(true, null)
                Log.d(TAG, "Compte utilisateur supprimé avec succès")
            } else {
                //erreur lors de la suppression du compte
                onComplete(false, task.exception?.message)
            }
        }
    } else {
        onComplete(false, "Aucun utilisateur connecté")
        }
    }
    //login user
    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //signup user
    suspend fun signUp(email: String, password: String, lastName: String, firstName: String): Result<Unit> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user ?: throw Exception("Erreur : Utilisateur non trouvé.")


            //Maj du profil user
            val profileUpdates = userProfileChangeRequest {
                displayName = "$firstName $lastName"
            }
            user.updateProfile(profileUpdates).await()

            //création de la biblio user
            val libraryResult = libraryRepository.createLibrary(user.uid, firstName, lastName)
            if (libraryResult.isFailure) {
                throw libraryResult.exceptionOrNull() ?: Exception("Erreur lors de la création de la bibliothèque")
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
