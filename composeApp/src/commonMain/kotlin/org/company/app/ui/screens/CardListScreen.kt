package org.company.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.company.app.model.CardDto
import org.company.app.repository.CardRepository
import org.company.app.theme.AppTheme
import org.company.app.ui.components.CardItem

class CardListScreenModel(
    private val repo: CardRepository = CardRepository()
) : ScreenModel {

    private val _state = MutableStateFlow<State>(State.Loading)
    val state: StateFlow<State> = _state

    sealed interface State {
        object Loading : State
        data class Data(val cards: List<CardDto>) : State
        data class Error(val msg: String) : State
    }

    init { refresh() }

    fun refresh() = screenModelScope.launch {
        _state.value = State.Loading
        runCatching { repo.list() }
            .onSuccess { _state.value = State.Data(it) }
            .onFailure { _state.value = State.Error(it.message ?: "Unknown") }
    }
}

class CardListScreen : Screen {

    @Composable
    override fun Content() {
        val model = rememberScreenModel { CardListScreenModel() }
        val state by model.state.collectAsState()

        val navigator = LocalNavigator.currentOrThrow

        AppTheme {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(onClick = { navigator.push(AddCardScreen(onSaved = { model.refresh() })) }) {
                        Text("+")
                    }
                }
            ) { inner ->
                Box(Modifier.fillMaxSize().padding(inner)) {

                    when (val s = state) {
                        is CardListScreenModel.State.Loading ->
                            CircularProgressIndicator(Modifier.align(Alignment.Center))

                        is CardListScreenModel.State.Error ->
                            Text(
                                s.msg,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.align(Alignment.Center)
                            )

                        is CardListScreenModel.State.Data -> {
                            val cards = s.cards
                            if (cards.isEmpty()) {
                                Text("No cards yet", Modifier.align(Alignment.Center))
                            } else {
                                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                                    items(
                                        items = cards,
                                        key = { it.id ?: 0 }
                                    ) { card ->
                                        CardItem(
                                            card = card,
                                            onOpen = { navigator.push(CardDetailsScreen(card.id!!)) },
                                            onEdit = { navigator.push(EditCardScreen(
                                                card.id!!,
                                                onSaved = { model.refresh() }
                                            )) },
                                            onDelete = { navigator.push(DeleteCardScreen(
                                                card.id!!,
                                                onDeleted = { model.refresh() }
                                            )) }
                                        )
                                        Spacer(Modifier.height(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
