package com.openclassrooms.hexagonal.games.screen.comment

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.hexagonal.games.domain.model.Comment
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentScreen(
    postId: String,
    viewModel: CommentViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajouter un commentaire") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = { Text("Écrivez votre commentaire...") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            viewModel.saveComment(
                                postId = postId,
                                comment = Comment(
                                    id = UUID.randomUUID().toString(),
                                    authorName = "",
                                    content = commentText,
                                )
                            )
                            Toast.makeText(context, "Commentaire ajouté avec succès", Toast.LENGTH_SHORT).show()
                            onBack()
                        } else {
                            Toast.makeText(context, "Veuillez entrer un commentaire", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Sauvegarder")
                }
            }
        }
    )
}
