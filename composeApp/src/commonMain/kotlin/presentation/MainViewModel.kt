package presentation

import SocketClient
import SocketInterface
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
    var connected: Boolean = false,
    var isServer: Boolean = false,
    val listMessage: ArrayList<Pair<String, Int>> = ArrayList()
)

class MainViewModel : ScreenModel, SocketInterface {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    fun startSocket() {
        screenModelScope.launch(Dispatchers.IO) {
            SocketServer().startServer(this@MainViewModel)
        }
    }

    fun connectToServer(ip: String) {
        screenModelScope.launch(Dispatchers.IO) {
//            delay(500)
            SocketClient().connectToServer(ip, this@MainViewModel)
        }
    }

    fun sendMessage(message: String) {
        screenModelScope.launch(Dispatchers.Default) {
            if (_uiState.value.isServer) {
                SocketServer().sendToAllClients(message)
            } else {
                SocketClient().sendToServer(message)
            }
        }
    }

    override fun successServerConnect() {
        println("Succes socket")
        _uiState.update {
            it.copy(connected = true, isServer = true)
        }
    }


    override fun errorServerConnect() {

    }

    override fun successClientConnect() {
        _uiState.update {
            it.copy(connected = true)
        }
    }

    override fun messageListener(message: Pair<String, Int>) {
        val newList = _uiState.value.listMessage
        newList.add(message)
        _uiState.update {
            it.copy(listMessage = newList)
        }
    }

}