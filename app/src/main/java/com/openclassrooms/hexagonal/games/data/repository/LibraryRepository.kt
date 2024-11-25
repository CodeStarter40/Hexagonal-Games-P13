package com.openclassrooms.hexagonal.games.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LibraryRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    //fonction de création de la biblio user
    suspend fun createLibrary(userId: String, firstName: String, lastName: String): Result<Unit> {
        return try {
            val userLibrary = hashMapOf(
                "userId" to userId,
                "firstName" to firstName,
                "lastName" to lastName
            )
            //use await pour rendre asynch
            firestore.collection("library").document(userId).set(userLibrary).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}