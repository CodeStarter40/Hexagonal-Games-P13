package com.openclassrooms.hexagonal.games.screen.account

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.hexagonal.games.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    viewModel: AccountViewModel = hiltViewModel(),
    onLogoutSuccess: () -> Unit,
    onDeleteSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Compte")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onBackClick()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.contentDescription_go_back)
                        )
                    }
                }
            )
        }
    ) { contentPadding ->
        val accountActionState by viewModel.accountActionState.collectAsStateWithLifecycle()

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                // Bouton pour se déconnecter
                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Se déconnecter")
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Bouton pour supprimer le compte
                Button(
                    onClick = { viewModel.deleteAccount() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Supprimer le compte")
                }

                //gestion des states
                when (accountActionState) {
                    is AccountActionState.LogoutSuccess -> {
                        LaunchedEffect(Unit) {
                            onLogoutSuccess()
                        }
                    }
                    is AccountActionState.DeleteSuccess -> {
                        LaunchedEffect(Unit) {
                            onDeleteSuccess()
                        }
                    }
                    is AccountActionState.Error -> {
                        val errorMessage = (accountActionState as AccountActionState.Error).message
                        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                    else -> Unit
                }
            }
        }
    }
}
