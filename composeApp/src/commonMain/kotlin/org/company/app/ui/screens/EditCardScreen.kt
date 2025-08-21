package org.company.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.*
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.company.app.Config
import org.company.app.model.CardDto
import org.company.app.network.HttpClientProvider
import org.company.app.repository.CardRepository
import org.company.app.repository.Storage
import org.company.app.util.pickImage
import org.jetbrains.skia.Image
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.company.app.theme.AppTheme

/**
 * Экран редактирования карточки с возможностью указать ссылку на изображение
 */
class EditCardScreen(
    private val cardId: Int,
    private val onSaved: () -> Unit
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val cardRepo = remember { CardRepository(HttpClientProvider.client) }
        var card by remember { mutableStateOf<CardDto?>(null) }
        var loading by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf<String?>(null) }
        var allowed by remember { mutableStateOf(false) }
        var newBytes by remember { mutableStateOf<ByteArray?>(null) }
        var previewBmp by remember { mutableStateOf<ImageBitmap?>(null) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(cardId) {
            runCatching { cardRepo.get(cardId) }
                .onSuccess { fetched ->
                    card = fetched
                    val me = Storage.currentUser
                    allowed = me != null && (me.isAdmin == true || fetched.author?.id == me.id)
                }
                .onFailure { ex -> error = ex.message ?: "Failed to load card" }
            loading = false
            if (!allowed && error == null) {
                error = "You are not allowed to edit this card"
            }
        }

        AppTheme {
            when {
                loading -> Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                error != null -> ErrorCard(message = error!!, onBack = { navigator.pop() })
                card != null && allowed -> EditContent(
                    card = card!!,
                    newBytes = newBytes,
                    previewBmp = previewBmp,
                    onChoose = { bytes ->
                        newBytes = bytes
                        previewBmp = bytes?.let { Image.makeFromEncoded(it).toComposeImageBitmap() }
                    },
                    onSave = { name, shortText, longText, imageUrl ->
                        scope.launch {
                            runCatching {
                                cardRepo.update(
                                    cardId,
                                    card!!.copy(
                                        name = name,
                                        shortText = shortText,
                                        longText = longText,
                                        imageUrl = imageUrl
                                    ),
                                    null
                                )
                            }
                                .onSuccess {
                                    onSaved()
                                    navigator.pop()
                                }
                                .onFailure { error = it.message ?: "Save failed" }
                        }
                    },
                    onBack = { navigator.pop() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditContent(
    card: CardDto,
    newBytes: ByteArray?,
    previewBmp: ImageBitmap?,
    onChoose: (ByteArray?) -> Unit,
    onSave: (String, String, String, String) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf(card.name) }
    var shortText by remember { mutableStateOf(card.shortText) }
    var longText by remember { mutableStateOf(card.longText) }
    var imageUrl by remember { mutableStateOf(card.imageUrl) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Image(
                            painter = painterResource("left_arrow.png"),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                title = { Text("Edit card #${card.id}") }
            )
        }
    ) { inner ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Current image", fontWeight = FontWeight.SemiBold)
            AsyncImage(
                model = "${Config.host}/storage/${card.imageUrl}",
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
            previewBmp?.let {
                Text("New image preview", fontWeight = FontWeight.SemiBold)
                Image(
                    bitmap = it,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

//            OutlinedButton(onClick = {
//                scope.launch {
//                    val result = pickImage()
//                    onChoose(result?.first)
//                }
//            }) {
//                Text("Choose new image")
//            }

            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Image URL") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = shortText,
                onValueChange = { shortText = it },
                label = { Text("Short text") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = longText,
                onValueChange = { longText = it },
                label = { Text("Long text") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { onSave(name, shortText, longText, imageUrl) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
fun AsyncImage(model: String, contentDescription: Nothing?, modifier: Modifier) {
    TODO("Not yet implemented")
}
