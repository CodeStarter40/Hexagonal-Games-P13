package com.openclassrooms.hexagonal.games.data.repository

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

//repository pour inclure les fun de gestion de compte comme la déconnexion et la suppression de compte via firebase

class UserRepository @Inject constructor(private val firebaseAuth: FirebaseAuth) {

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
            } else {
                //erreur lors de la suppression du compte
                onComplete(false, task.exception?.message)
            }
        }
    } else {
        onComplete(false, "Aucun utilisateur connecté")
        }
    }
}
