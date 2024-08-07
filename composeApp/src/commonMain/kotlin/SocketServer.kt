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


    fun startServer(socketInterface: SocketInterface) = runBlocking {
        try {
            val selector = SelectorManager(Dispatchers.Default)
            serverSocket = aSocket(selector).tcp().bind(port = port)
            println("Server started on port $port --- ${serverSocket.localAddress} -- ${serverSocket.isClosed}")
            if (!serverSocket.isClosed) {
                socketInterface.successServerConnect()
            } else {
                socketInterface.errorServerConnect()
            }

            while (true) {
                val socket = serverSocket.accept()

                clients.add(socket)
                handleClient(socket, socketInterface)
            }
        } catch (e: Exception) {
            e.printStack()
            socketInterface.errorServerConnect()
        }
    }


    private suspend fun handleClient(socket: Socket, socketInterface: SocketInterface) {
        withContext(Dispatchers.Default) {
            val input = socket.openReadChannel()
            val output = socket.openWriteChannel(autoFlush = true)

            // Leer mensaje del cliente
            val clientMessage = input.readUTF8Line(limit = 1024)
            println("Received from client: $clientMessage")
            socketInterface.messageListener(Pair(clientMessage.toString(), 2))

            // Enviar respuesta al cliente
            val responseMessage = "First execution $clientMessage"
            output.writeStringUtf8("$responseMessage\n")

            while (true) {
                val clientMessage = input.readUTF8Line(limit = 1024)
                if (clientMessage != null) {
                    println("Received from client: $clientMessage")
                    socketInterface.messageListener(Pair(clientMessage.toString(), 2))
                }
            }

            socket.close()
        }
    }

    suspend fun sendToAllClients(message: String) {
        withContext(Dispatchers.Default) {
            clients.forEach { socket ->
                val output = socket.openWriteChannel(autoFlush = true)
                output.writeStringUtf8("$message\n")
            }
        }
    }


}