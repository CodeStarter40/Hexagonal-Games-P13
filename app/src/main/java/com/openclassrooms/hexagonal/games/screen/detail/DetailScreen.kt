package com.openclassrooms.hexagonal.games.screen.detail

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.model.Comment
import com.openclassrooms.hexagonal.games.domain.model.Post
import java.text.SimpleDateFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    postId: String,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onNavigateToComment: (String) -> Unit,
) {

    val context = LocalContext.current
    val scaffoldState = rememberBottomSheetScaffoldState()

    //appel de fonction de recuperation du post lorsque que le composable est loaded
    LaunchedEffect(Unit) {
        viewModel.getPost(postId)
        viewModel.loadComments(postId)
    }

    val post by viewModel.post.collectAsStateWithLifecycle(initialValue = null)
    val comments by viewModel.comments.collectAsStateWithLifecycle(initialValue = null)
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetContent = {
                //config du volet avec Scroll
                BoxWithConstraints {
                    val maxHeight = maxHeight * 0.5f
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = maxHeight)
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 16.dp,start = 10.dp)
                    ) {
                        Text(
                            text = "Commentaires",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        comments?.let { CommentList(comments = it) }
                    }
                }
            },
            sheetPeekHeight = 68.dp, //hauteur visible par défaut
            topBar = {
                TopAppBar(
                    title = { Text("") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.contentDescription_go_back)
                            )
                        }
                    },
                    modifier = Modifier.height(48.dp)
                )
            },
            content = { paddingValues ->
                //content principal
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    post?.let {
                        DetailContent(
                            post = it,
                            modifier = modifier.weight(1f)
                        )
                    }
                }
            }
        )

        //bouton flottant pour ajouter un commentaire
        FloatingActionButton(
            onClick = {
                onNavigateToComment(postId)
                Toast.makeText(context, "Ajoutez un commentaire constructif !", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(id = R.string.description_button_add)
            )
        }
    }
/*
    //ouvre le bottom sheet si il y a des commentaires sur un maximum de 0.1f
    LaunchedEffect(comments) {
        coroutineScope.launch {
            if (comments?.isNotEmpty() == true) {
                scaffoldState.bottomSheetState.expand()
            } else {
                scaffoldState.bottomSheetState.partialExpand()
            }
        }
    }

 */
}


@Composable
fun DetailContent(
    post: Post,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        //affiche le nom de l'auteur
        post.author?.let {
            Text(
                text = "Auteur: ${it.firstname} ${it.lastname}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 4.dp,top = 5.dp)
            )
            //convertion timestamp en date
            val dateFormat = SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault()).format(post.timestamp)

            //affiche la date de parrution du post
            Text(
                text = "Publié le $dateFormat",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        //affiche le titre du post
        Text(
            text = post.title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        //si image, l'affiche sinon rien n'est affiché
        post.photoUrl?.let {
            if (it.isNotEmpty()) {
                AsyncImage(
                    model = it,
                    placeholder = ColorPainter(MaterialTheme.colorScheme.onSurface),
                    contentDescription = stringResource(id = R.string.contentDescription_post_image),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .aspectRatio(ratio = 16 / 9f)
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                )
            }
        }

        //description du post
        Text(
            text = post.description ?: "",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}
@Composable
fun CommentItem(comment: Comment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            Text(
                text = comment.authorName,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            val dateFormat = comment.timestamp?.let {
                SimpleDateFormat("dd/MM/yyyy, HH:mm", Locale.getDefault()).format(it)
            }
            Text(
                text = "Publié le : $dateFormat",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = comment.content,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
@Composable
fun CommentList(comments: List<Comment>) {

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)) {
        if (comments.isEmpty()) {
            Text(
                text = "Aucun commentaire pour l'instant.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            comments.forEach { comment ->
                CommentItem(comment = comment)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

