package com.openclassrooms.hexagonal.games.screen.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    //récupérer l'état de l'interface utilisateur
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    //champs pour les entrées utilisateur
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUpMode by remember { mutableStateOf(false) } //gere le mode inscription ou connexion

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            //Email
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            //mot de Passe
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mot de passe") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            //bouton se Connecter ou s'inscrire
            Button(
                onClick = {
                    if (isSignUpMode) {
                        viewModel.signUp(email, password)
                    } else {
                        viewModel.login(email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSignUpMode) "S'inscrire" else "Se connecter")
            }

            Spacer(modifier = Modifier.height(8.dp))

            //bouton pour basculer entre Inscription et Connexion
            TextButton(
                onClick = { isSignUpMode = !isSignUpMode },
            ) {
                Text(if (isSignUpMode) "Déjà un compte ? Connectez-vous" else "Pas de compte ? Inscrivez-vous")
            }

            Spacer(modifier = Modifier.height(16.dp))

            //gestion des différents états de l'interface utilisateur
            when (uiState) {
                is LoginUiState.Loading -> CircularProgressIndicator()
                is LoginUiState.Success -> {
                    //si succès, rediriger vers la page d'accueil
                    LaunchedEffect(Unit) {
                        onLoginSuccess()
                    }
                }
                //si error, afficher le message d'erreur
                is LoginUiState.Error -> {
                    val errorMessage = (uiState as LoginUiState.Error).message
                    Text(text = errorMessage)
                }
                else -> Unit
            }
        }
    }
}
