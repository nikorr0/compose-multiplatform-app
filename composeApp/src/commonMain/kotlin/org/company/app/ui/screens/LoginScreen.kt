package org.company.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.*
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.company.app.network.HttpClientProvider
import org.company.app.repository.AuthRepository
import org.company.app.repository.Storage

class LoginScreenModel(
    private val authRepo: AuthRepository = AuthRepository(HttpClientProvider.client)
) : ScreenModel {
    sealed interface UiState {
        object Idle : UiState
        object Loading : UiState
        object Success : UiState
        data class Error(val message: String) : UiState
    }

    private val _state = mutableStateOf<UiState>(UiState.Idle)
    val state: State<UiState> = _state

    var email by mutableStateOf("")
    var password by mutableStateOf("")

    fun onLoginClick(onSuccess: () -> Unit) = screenModelScope.launch {
        _state.value = UiState.Loading
        runCatching {
            authRepo.login(email.trim(), password)
        }.onSuccess { token ->
            Storage.token = token
            _state.value = UiState.Success
            onSuccess()
        }.onFailure { ex ->
            _state.value = UiState.Error(ex.localizedMessage ?: "Login failed")
        }
    }
}

@Composable
fun LoginScreenContent(model: LoginScreenModel, onLogin: () -> Unit) {
    val uiState by model.state
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            Modifier
                .widthIn(max = 300.dp)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Sign in", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = model.email,
                onValueChange = { model.email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = model.password,
                onValueChange = { model.password = it },
                label = { Text("Password") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Button(
                onClick = { onLogin() },
                enabled = uiState != LoginScreenModel.UiState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState == LoginScreenModel.UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Login")
                }
            }

            if (uiState is LoginScreenModel.UiState.Error) {
                Text(
                    text = (uiState as LoginScreenModel.UiState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val model = rememberScreenModel { LoginScreenModel() }
        val navigator = LocalNavigator.currentOrThrow

        LoginScreenContent(
            model = model,
            onLogin = {
                model.onLoginClick {
                    navigator.replaceAll(CardListScreen())
                    }
                })
    }
}
