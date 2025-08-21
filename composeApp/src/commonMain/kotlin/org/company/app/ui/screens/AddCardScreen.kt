package org.company.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
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
import org.company.app.model.CardDto
import org.company.app.network.HttpClientProvider
import org.company.app.repository.CardRepository
import org.company.app.repository.Storage
import org.company.app.theme.AppTheme
import org.jetbrains.skia.Image
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.launch
import org.company.app.util.pickImage

/**
 * Экран создания новой карточки
 */
class AddCardScreen(
    private val onSaved: () -> Unit
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val repo = remember { CardRepository(HttpClientProvider.client) }
        var name by remember { mutableStateOf("") }
        var shortText by remember { mutableStateOf("") }
        var longText by remember { mutableStateOf("") }
        var imageBytes by remember { mutableStateOf<ByteArray?>(null) }
        var previewBmp by remember { mutableStateOf<ImageBitmap?>(null) }
        var loading by remember { mutableStateOf(false) }
        var error by remember { mutableStateOf<String?>(null) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(imageBytes) {
            previewBmp = imageBytes?.let { img ->
                Image.makeFromEncoded(img).toComposeImageBitmap()
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
                        title = { Text("New Card") }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            loading = true
                            error = null
                            scope.launch {
                                try {
                                    val dto = CardDto(
                                        id = null,
                                        name = name,
                                        shortText = shortText,
                                        longText = longText,
                                        imageUrl = "",
                                        author = Storage.currentUser
                                    )
                                    repo.create(dto, imageBytes)
                                    onSaved()
                                    navigator.pop()
                                } catch (ex: Exception) {
                                    error = ex.message
                                    loading = false
                                }
                            }
                        }
                    ) { Text("Save") }
                }
            ) { inner ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(inner)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (error != null) {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = shortText,
                        onValueChange = { shortText = it },
                        label = { Text("Short Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = longText,
                        onValueChange = { longText = it },
                        label = { Text("Long Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )
                    Button(onClick = {
                        scope.launch {
                            val result = pickImage()
                            imageBytes = result?.first
                        }
                    }) {
                        Text("Choose Image")
                    }
                    previewBmp?.let {
                        Text("Image Preview", fontWeight = FontWeight.SemiBold)
                        Image(
                            bitmap = it,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            // contentScale = ContentScale.Crop
                        )
                    }
                    if (loading) {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}
