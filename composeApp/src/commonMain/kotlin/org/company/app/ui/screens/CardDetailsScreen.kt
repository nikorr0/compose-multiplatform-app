package org.company.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.*
import org.company.app.model.CardDto
import org.company.app.model.CommentDto
import org.company.app.network.HttpClientProvider
import org.company.app.repository.CardRepository
import org.company.app.theme.AppTheme
import kotlinx.coroutines.launch
import org.company.app.Config

class CardDetailsScreen(private val cardId: Int) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        var card by remember { mutableStateOf<CardDto?>(null) }
        var error by remember { mutableStateOf<String?>(null) }
        var loading by remember { mutableStateOf(true) }
        val repo = remember { CardRepository(HttpClientProvider.client) }

        LaunchedEffect(cardId) {
            runCatching { repo.get(cardId) }
                .onSuccess {
                    card = it
                    loading = false
                }
                .onFailure {
                    error = it.message
                    loading = false
                }
        }

        AppTheme {
            when {
                loading -> Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = error ?: "Unknown error", color = MaterialTheme.colorScheme.error)
                }
                card != null -> Scaffold(
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
                            title = { Text(text = card!!.name) }
                        )
                    }
                ) { inner ->
                    DetailsContent(card!!, Modifier.padding(inner))
                }
            }
        }
    }
}

@Composable
private fun DetailsContent(card: CardDto, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            AsyncImage(
                model = "${Config.host}/storage/${card.imageUrl}",
                contentDescription = card.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(card.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(card.shortText, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text(card.longText, style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))
            Text("Author: ${card.author?.name}", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(16.dp))
            Divider()
            Spacer(Modifier.height(8.dp))
            Text("Comments:", style = MaterialTheme.typography.titleMedium)
        }
        if (card.comments.isEmpty()) {
            item {
                Text(
                    "No comments yet",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(8.dp)
                )
            }
        } else {
            items(card.comments) { comment: CommentDto ->
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(comment.author.name, fontWeight = FontWeight.SemiBold)
                    Text(comment.content, style = MaterialTheme.typography.bodySmall)
                    Divider()
                }
            }
        }
    }
}
