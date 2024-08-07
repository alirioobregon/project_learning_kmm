import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.util.KtorExperimentalAPI
import io.ktor.utils.io.printStack
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class SocketClient() {
    private val port = 12345
    private var socket: Socket? = null

    fun connectToServer(serverIp: String, socketInterface: SocketInterface) = runBlocking {
        try {
            val selector = SelectorManager(Dispatchers.Default)
            socket = aSocket(selector).tcp().connect(serverIp, port)
            socketInterface.successClientConnect()

            val input = socket?.openReadChannel()
            val output = socket?.openWriteChannel(autoFlush = true)

            // Enviar mensaje al servidor
            val messageToSend = "Test!"
            output?.writeStringUtf8("$messageToSend\n")

            // Leer respuesta del servidor
//            val responseMessage = input?.readUTF8Line(limit = 1024)
//            println("Received 0 from server: $responseMessage")

            // Leer mensajes del servidor en un bucle
            while (true) {
                val serverMessage = input?.readUTF8Line(limit = 1024)
                if (serverMessage != null) {
                    println("Received 1 from server: $serverMessage")
                    socketInterface.messageListener(Pair(serverMessage.toString(), 1))
                }
            }

            socket?.close()
        } catch (e: Exception) {
            println("Connection failed: ${e}")
        }
    }

    suspend fun sendToServer(message: String) {
//        withContext(Dispatchers.Default) {
//            clients.forEach { socket ->
        val output = socket?.openWriteChannel(autoFlush = true)
        output?.writeStringUtf8("$message\n")
//            }
//        }
    }
}
