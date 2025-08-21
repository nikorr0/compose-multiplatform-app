package org.company.app.ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import org.company.app.repository.Storage
import org.company.app.ui.screens.CardListScreen
import org.company.app.ui.screens.LoginScreen
import org.company.app.theme.AppTheme

@Composable
fun MainApp() {

    AppTheme {
        Navigator(
            screen = if (Storage.token.isNullOrBlank()) {
                LoginScreen()
            } else {
                CardListScreen()
            }
        )
    }
}