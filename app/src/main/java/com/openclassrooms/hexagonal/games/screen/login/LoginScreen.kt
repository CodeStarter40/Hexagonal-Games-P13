package com.openclassrooms.hexagonal.games.screen.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.hexagonal.games.R
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource

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
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var isSignUpMode by remember { mutableStateOf(false) } //gere le mode inscription ou connexion

    val context = LocalContext.current
    //etat de defilement
    val scrollState = rememberScrollState()
    //recupere le status du clavier
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            //logo de l'application
            Image(
                painter = painterResource(id = R.drawable.ic_launcher),
                contentDescription = "logo de l'application",
                modifier = Modifier
                    .size(250.dp)
                    .padding(bottom = 25.dp)
            )

            if (isSignUpMode) {
                //champs prenom affiché si en mode signup
                TextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text(text = stringResource(id = R.string.firstname)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text(text = stringResource(id = R.string.lastname)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                    )
                Spacer(modifier = Modifier.height(8.dp))
            }
            //Email
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = stringResource(id = R.string.email)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            //mot de Passe
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = stringResource(id = R.string.password)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            //bouton se Connecter ou s'inscrire
            Button(
                onClick = {
                    keyboardController?.hide() //cache le clavier pour laisser apparaitre les champs d'information
                    if (isSignUpMode) {
                        viewModel.signUp(email, password, lastName, firstName)
                    } else {
                        viewModel.login(email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isSignUpMode) { stringResource(id = R.string.register_button) } else { stringResource(id = R.string.connect_button) })
            }

            Spacer(modifier = Modifier.height(8.dp))

            //bouton pour basculer entre Inscription et Connexion
            TextButton(
                onClick = { isSignUpMode = !isSignUpMode },
            ) {
                Text(if (isSignUpMode) {stringResource(id = R.string.already_account)} else {stringResource(id = R.string.no_account)})
            }

            Spacer(modifier = Modifier.height(25.dp))

            //gestion des différents états de l'interface utilisateur
            when (uiState) {
                is LoginUiState.Loading -> CircularProgressIndicator()
                is LoginUiState.Success -> {
                    //si succès, rediriger vers la page d'accueil
                    LaunchedEffect(Unit) {
                        Toast.makeText(context,R.string.connexion_granted, Toast.LENGTH_SHORT).show()
                        onLoginSuccess()
                    }
                }
                //si error, afficher le message d'erreur
                is LoginUiState.Error -> {
                    Text(text = stringResource(id = R.string.connexion_failed),
                        color = MaterialTheme.colorScheme.error)

                }
                else -> Unit
            }
        }
    }
}
