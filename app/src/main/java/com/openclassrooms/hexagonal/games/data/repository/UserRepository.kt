package com.openclassrooms.hexagonal.games.data.repository

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

//repository pour inclure les fun de gestion de compte comme la déconnexion et la suppression de compte via firebase

class UserRepository @Inject constructor(private val firebaseAuth: FirebaseAuth, private val libraryRepository: LibraryRepository,private val firestore: FirebaseFirestore) {

    //déconnexion du user actuel
    fun signOut() {
        firebaseAuth.signOut()
    }

    //suppression du compte actuel
    suspend fun deleteAccount(): Result<Unit> {
        return try {
            val currentUser = firebaseAuth.currentUser
                ?: return Result.failure(Exception("Aucun utilisateur connecté"))

            //duppression des données de l'utilisateur dans Firestore
            deleteUserData(currentUser.uid)
            deletePosts(currentUser.uid)
            Log.d("USERREPOSITORY-DELETEACCOUNT-FUN", "deleteUserData() exec")

            //suppression du compte FirebaseAuth
            currentUser.delete().await()
            Log.d("USERREPOSITORY-DELETEACCOUNT-FUN", "deleteAccount() exec")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("USERREPOSITORY-DELETEACCOUNT-FUN", "Erreur lors de la suppression du compte", e)
            Result.failure(e)
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

    private suspend fun deleteUserData(userId: String) {
        try {
            //suppression des données de l'utilisateur dans Firestore libray et posts
            firestore.collection("library").document(userId).delete().await()

        } catch (e: Exception) {
            throw Exception("Erreur de la suppression des données ${e.message}")
        }
    }

    private suspend fun deletePosts(uid: String) {
        try {
            val querySnapshot = firestore.collection("posts")
                .whereEqualTo("uid", uid)
                .get()
                .await()
            for (document in querySnapshot.documents) {
                document.reference.delete().await()
            }
            Log.d("USERREPOSITORY-DELETEPOSTS-FUN", "Les posts de l'utilisateur ont été supprimés")
        } catch (e: Exception) {
            throw Exception("Erreur de la suppression des posts ${e.message}")
        }
    }
}
