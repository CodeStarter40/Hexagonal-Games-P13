package com.openclassrooms.hexagonal.games.data.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.openclassrooms.hexagonal.games.domain.model.Comment
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID


class CommentImpFirebase(firestore: FirebaseFirestore) : CommentApi {

    private val db = FirebaseFirestore.getInstance()
    private val commentsCollection = db.collection("comments")


    override fun getComments(postId: String): Flow<List<Comment>> = callbackFlow {
        //abo sur la collection "comments" pour récupérer les commentaires d'un post spécifique
        val listener = commentsCollection
            .whereEqualTo("postId", postId)
            .orderBy("timestamp", Query.Direction.ASCENDING) //trier par date de création
            .addSnapshotListener { snapshot, error ->
                //en cas d'erreur
                if (error != null) {
                    Log.e(
                        "FIRESTORE",
                        "Erreur lors de la récupération des commentaires : ${error.message}"
                    )
                    close(error)
                    return@addSnapshotListener
                }

                //si les données sont récupérées
                if (snapshot != null) {
                    val comments = snapshot.documents.mapNotNull {
                        try {
                            it.toObject(Comment::class.java) //map le document vers un objet Comment
                        } catch (e: Exception) {
                            Log.e(
                                "FIRESTORE",
                                "Erreur de mapping de document vers Comment: ${e.message}"
                            )
                            null //ignore le document si erreur de mapping
                        }
                    }
                    Log.d("FIRESTORE", "Commentaires récupérés : ${comments.size}")
                    trySend(comments).isSuccess //envoyer la liste des commentaires
                } else {
                    Log.d("FIRESTORE", "Aucune donnée trouvée")
                }
            }

        awaitClose { listener.remove() } //suppression du listener et fermeture du flow
    }

    override suspend fun addComment(postId: String, comment: Comment) {
        val commentId = UUID.randomUUID().toString()  //générer un ID unique
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            Log.e("Firestore", "Erreur : Aucun utilisateur connecté.")
            return
        }

        val commentWithDetails = comment.copy(
            id = commentId,
            authorName = currentUser.displayName ?:"",
            postId = postId,
            uid = currentUser.uid,
            timestamp = null
        )

        commentsCollection.document(commentId)
            .set(commentWithDetails)
            .addOnSuccessListener {
                Log.d("Firestore", "Commentaire ajouté avec succès: ${commentWithDetails.id}")
            }
            .addOnFailureListener { e ->
                Log.d("Firestore", "Erreur lors de l'ajout du commentaire", e)
            }
    }
}