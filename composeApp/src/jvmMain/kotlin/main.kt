import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.plugin
import org.company.app.Config
import org.company.app.network.HttpClientProvider
import org.company.app.repository.Storage
import org.company.app.session.FileOperator
import org.company.app.session.SessionStorage
import java.awt.Dimension
// import org.company.app.App
import org.company.app.ui.MainApp
import org.company.app.ui.screens.LoginScreen

fun main() = application {
    Window(
        title = "CountryCardsApp",
        state = rememberWindowState(width = 800.dp, height = 600.dp),
        onCloseRequest = ::exitApplication,
    ) {
        window.minimumSize = Dimension(350, 600)
        MainApp()
    }
}

