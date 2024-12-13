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
                    Text(text = stringResource(id = R.string.account))
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
                    Text(text=stringResource(id = R.string.logout_button_account_page))
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Delete button
                Button(
                    onClick = {
                        showDialog = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text= stringResource(id = R.string.delete_button_account_page))
                }

                //gestion des states et toast message
                when (accountActionState) {
                    is AccountActionState.LogoutSuccess -> {
                        LaunchedEffect(Unit) {
                            Toast.makeText(context,R.string.disconnect_user, Toast.LENGTH_LONG).show()
                            onLogoutSuccess()
                        }
                    }
                    is AccountActionState.DeleteSuccess -> {
                        LaunchedEffect(Unit) {
                            Toast.makeText(context,R.string.data_deleted, Toast.LENGTH_LONG).show()
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
            Text(text = stringResource(id = R.string.delete_account))
        },
        text = {
            Text(text = stringResource(id = R.string.deletion_confirmation))
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(text = stringResource(id = R.string.cancel))
            }
        }
    )
}