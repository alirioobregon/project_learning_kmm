import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.isClosed
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.printStack
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class SocketServer {
    private val port = 12345
    private lateinit var serverSocket: ServerSocket
    private val clients = mutableSetOf<Socket>()


    fun startServer(): Boolean = runBlocking {
        try {
            val selector = SelectorManager(Dispatchers.Default)
            serverSocket = aSocket(selector).tcp().bind(port = port)
            println("Server started on port $port --- ${serverSocket.localAddress} -- ${serverSocket.isClosed}")

            while (true) {
                val socket = serverSocket.accept()

                clients.add(socket)
                handleClient(socket)
            }
        } catch (e: Exception) {
            e.printStack()
        }
        return@runBlocking !serverSocket.isClosed
    }


    private suspend fun handleClient(socket: Socket) {
        withContext(Dispatchers.Default) {
            val input = socket.openReadChannel()
            val output = socket.openWriteChannel(autoFlush = true)

            // Leer mensaje del cliente
            val clientMessage = input.readUTF8Line(limit = 1024)
            println("Received from client: $clientMessage")

            // Enviar respuesta al cliente
            val responseMessage = "Bienvenido $clientMessage, Como vas?"
            output.writeStringUtf8("$responseMessage\n")

            socket.close()
        }
    }

    private suspend fun sendToAllClients(message: String) {
        withContext(Dispatchers.Default) {
            clients.forEach { socket ->
                val output = socket.openWriteChannel(autoFlush = true)
                output.writeStringUtf8("$message\n")
            }
        }
    }


}