package com.openclassrooms.hexagonal.games.data.service

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.openclassrooms.hexagonal.games.domain.model.Post
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.UUID

class PostImpFirebase: PostApi  {

    private val db = FirebaseFirestore.getInstance()
    private val postsCollection = db.collection("posts")


    override fun getPostsOrderByCreationDateDesc(): Flow<List<Post>> = callbackFlow {
        val listener = postsCollection.orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FIRESTORE", "Erreur lors de la récupération des posts : ${error.message}")
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val posts = snapshot.documents.mapNotNull {
                        try {
                            it.toObject(Post::class.java)
                        } catch (e: Exception) {
                            Log.e("FIRESTORE", "Erreur de mapping de document vers Post: ${e.message}")
                            null
                        }
                    }
                    Log.e("FIRESTORE", "Posts récupérés : ${posts.size}")
                    trySend(posts).isSuccess
                } else {
                    Log.e("FIRESTORE", "Aucune donnée trouvée")
                }
            }

        awaitClose { listener.remove() }
    }


    override fun addPost(post: Post) {
        val postId = UUID.randomUUID().toString()  //generer un id unique du post
        val postWithId = post.copy(id = postId)    //add un id au post avant d'ajouter

        postsCollection.document(postId)
            .set(postWithId)
            .addOnSuccessListener {
                Log.d("Firestore", "Post ajouté avec succès: ${postWithId.id}")
            }
            .addOnFailureListener { e ->
                Log.d("Firestore", "Erreur lors de l'ajout du post", e)
            }
    }
}