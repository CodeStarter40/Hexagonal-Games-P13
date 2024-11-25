package com.openclassrooms.hexagonal.games.screen.account

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

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
                // Disconnect Button
                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Se déconnecter")
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Delete button
                Button(
                    onClick = {
                        showDialog = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Supprimer le compte")
                }

                //gestion des states et toast message
                when (accountActionState) {
                    is AccountActionState.LogoutSuccess -> {
                        LaunchedEffect(Unit) {
                            Toast.makeText(context, "Déconnexion réussie", Toast.LENGTH_SHORT).show()
                            onLogoutSuccess()
                        }
                    }
                    is AccountActionState.DeleteSuccess -> {
                        LaunchedEffect(Unit) {
                            Toast.makeText(context, "Compte supprimé avec succès", Toast.LENGTH_SHORT).show()
                            onDeleteSuccess()
                        }
                    }
                    is AccountActionState.Error -> {
                        val errorMessage = (accountActionState as AccountActionState.Error).message
                        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
    }
    //action shooDialog
    if (showDialog) {
        showDeleteAccountConfirmationDialog(
            onConfirm = {
                viewModel.deleteAccount()
                showDialog = false
            },
            onCancel = {
                showDialog = false
            }
        )
    }
}

@Composable
fun showDeleteAccountConfirmationDialog(onConfirm: () -> Unit, onCancel: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onCancel() },
        title = {
            Text(text = "Supprimer le compte")
        },
        text = {
            Text("Etes vous sûr de vouloir supprimer votre compte ? Cette action est irréversible !")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Supprimer")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("Annuler")
            }
        }
    )
}