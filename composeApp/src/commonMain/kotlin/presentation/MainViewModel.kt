package presentation

import SocketClient
import SocketServer
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MainUiState(
    var connected: Boolean = false
)

class MainViewModel : ScreenModel {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    fun startSocket() {
        screenModelScope.launch(Dispatchers.IO) {
            when (val response = SocketServer().startServer()) {
                true -> {
                    _uiState.update { it.copy(connected = response) }
                }

                false -> {}
            }
        }
    }

    fun connectToServer() {
        screenModelScope.launch(Dispatchers.IO) {
            delay(500)
            SocketClient("192.168.100.114").connectToServer()
        }
    }

}