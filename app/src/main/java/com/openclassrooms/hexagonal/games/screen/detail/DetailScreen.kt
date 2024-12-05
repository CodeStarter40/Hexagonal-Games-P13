package com.openclassrooms.hexagonal.games.screen.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.openclassrooms.hexagonal.games.R
import com.openclassrooms.hexagonal.games.domain.model.Post
import androidx.lifecycle.compose.collectAsStateWithLifecycle




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    postId: String,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    //appel de fonction de recuperation du post lorsque que le composable est loaded
    LaunchedEffect(Unit) {
        viewModel.getPost(postId)
    }

    val post by viewModel.post.collectAsStateWithLifecycle(initialValue = null)
    Scaffold(
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
                }
            )
        },
        content = { paddingValues ->
            post?.let {
                DetailContent(
                    post = it,
                    modifier = modifier.padding(paddingValues)
                )
            }
        }
    )
}

@Composable
fun DetailContent(
    post: Post,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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
                        .height(200.dp)
                        .padding(bottom = 10.dp)
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