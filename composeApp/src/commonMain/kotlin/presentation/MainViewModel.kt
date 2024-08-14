package presentation

import SocketClient
import SocketInterface
import SocketServer
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import models.Messages

data class MainUiState(
    var connected: Boolean = false,
    var isServer: Boolean = false,
    var listMessages: MutableList<Messages> = mutableListOf()
)

class MainViewModel : ScreenModel, SocketInterface {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    fun startSocket(ip: String) {
        screenModelScope.launch(Dispatchers.IO) {
            SocketServer.getInstance().startServer(this@MainViewModel, ip)
        }
    }

    fun connectToServer(ip: String) {
        screenModelScope.launch(Dispatchers.IO) {
//            delay(500)
            SocketClient.getInstance().connectToServer2(ip, this@MainViewModel)
        }
    }

    fun sendMessage(message: String) {
        screenModelScope.launch(Dispatchers.IO) {
            if (_uiState.value.isServer) {
                SocketServer.getInstance().sendToAllClients(message, this@MainViewModel)
            } else {
                println("Aliii send to server")
                SocketClient.getInstance().sendToServer(message, this@MainViewModel)
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
        _uiState.update {
            it.copy(connected = false, isServer = false)
        }
    }

    override fun successClientConnect() {
        _uiState.update {
            it.copy(connected = true)
        }
    }

    override fun messageListener(messages: Messages) {
        _uiState.update { current ->
//            current.listMessage.add(message)
            val newList = current.listMessages + messages
            current.copy(listMessages = ArrayList(newList))
        }
        println(_uiState.value.listMessages)
    }

}