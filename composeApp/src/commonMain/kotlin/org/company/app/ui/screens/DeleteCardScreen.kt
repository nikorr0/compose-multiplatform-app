package org.company.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.*
import kotlinx.coroutines.launch
import org.company.app.model.CardDto
import org.company.app.model.MessageResponseDto
import org.company.app.network.HttpClientProvider
import org.company.app.repository.CardRepository
import org.company.app.theme.AppTheme
import androidx.compose.foundation.Image

class DeleteCardScreen(
    private val cardId: Int,
    private val onDeleted: () -> Unit
) : Screen {
    private enum class DeleteState { Loading, Loaded, Deleting, Error }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val repo = remember { CardRepository(HttpClientProvider.client) }
        var card by remember { mutableStateOf<CardDto?>(null) }
        var state by remember { mutableStateOf(DeleteState.Loading) }
        var errorMsg by remember { mutableStateOf<String?>(null) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(cardId) {
            runCatching { repo.get(cardId) }
                .onSuccess {
                    card = it
                    state = DeleteState.Loaded
                }
                .onFailure {
                    errorMsg = it.message ?: "Failed to load card"
                    state = DeleteState.Error
                }
        }

        AppTheme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        navigationIcon = {
                            IconButton(onClick = { navigator.pop() }) {
                                Image(
                                    painter = painterResource("left_arrow.png"),
                                    contentDescription = "Back",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        },
                        title = { Text("Delete Card") }
                    )
                }
            ) { inner ->
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(inner)
                        .padding(16.dp)
                ) {
                    when (state) {
                        DeleteState.Loading ->
                            CircularProgressIndicator(Modifier.align(Alignment.Center))

                        DeleteState.Error ->
                            Text(
                                text = errorMsg ?: "Error",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.align(Alignment.Center)
                            )

                        DeleteState.Loaded, DeleteState.Deleting -> {
                            Column(
                                Modifier.align(Alignment.Center),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = card?.name
                                        ?.let { "Are you sure you want to delete the card '$it'?" }
                                        ?: "Delete this card?",
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center
                                )

                                errorMsg?.let { msg ->
                                    Text(msg, color = MaterialTheme.colorScheme.error)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Button(
                                        onClick = {
                                            state = DeleteState.Deleting
                                            errorMsg = null
                                            scope.launch {
                                                runCatching {
                                                    repo.delete(cardId)
                                                }
                                                    .onSuccess { resp: MessageResponseDto ->
                                                        if (resp.status == 403) {
                                                            errorMsg = resp.message
                                                            state = DeleteState.Error
                                                        } else {
                                                            onDeleted()
                                                            navigator.pop()
                                                        }
                                                    }
                                                    .onFailure {
                                                        onDeleted()
                                                        navigator.pop()
                                                    }
                                            }
                                        }
                                    ) {
                                        if (state == DeleteState.Deleting) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp)
                                            )
                                        } else {
                                            Text("Delete")
                                        }
                                    }
                                }
                                OutlinedButton(
                                    onClick = { navigator.pop() }
                                ) {
                                    Text("Cancel")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

