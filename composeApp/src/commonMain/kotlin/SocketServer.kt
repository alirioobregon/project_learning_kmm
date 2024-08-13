import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.isClosed
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.core.toByteArray
import io.ktor.utils.io.printStack
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class SocketServer {
    private val port = 12342
    private lateinit var serverSocket: ServerSocket
    private val clients = mutableSetOf<Pair<Socket, ByteWriteChannel?>>()


    suspend fun startServer(socketInterface: SocketInterface, ip: String) {
        try {
            withContext(Dispatchers.IO) {
                val selector = SelectorManager(Dispatchers.Default)
                serverSocket = aSocket(selector).tcp().bind(port = port)
                println("Aliii Server started on port $ip $port --- ${serverSocket.localAddress}")

                if (!serverSocket.isClosed) {
                    socketInterface.successServerConnect()
                } else {
                    socketInterface.errorServerConnect()
                }

                while (true) {
                    val socket = serverSocket.accept()
                    println("Aliii Server acepted ${socket.localAddress} --- ${socket.remoteAddress}")
                    launch {
                        handleClient(socket, socketInterface)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStack()
            socketInterface.errorServerConnect()
        }
    }

    private suspend fun handleClient(socket: Socket, socketInterface: SocketInterface) {
        try {

            // Coroutine para manejar la recepción de mensajes del cliente
//            withContext(Dispatchers.IO) {
                val input = socket.openReadChannel()
                val output = socket.openWriteChannel(autoFlush = true)

                val pair = Pair(socket, output)
                clients.add(pair)

                // Enviar respuesta inicial al cliente
                output.writeStringUtf8("First execution\n")
                while (true) {
                    val clientMessage = input.readUTF8Line(limit = 16000)
                    println("Aliiii Received from client: ${clientMessage.toString()}")
                    if (clientMessage != null) {
                        println("Aliiii Received from client: ${clientMessage.toString()}")
                        socketInterface.messageListener(Pair(clientMessage, 1))
                    } else {
                        // Si el cliente envía null, significa que se desconectó
                        break
                    }
                }
//            }
        } catch (e: Exception) {
            e.printStack()
        } finally {
            // Limpiar recursos y eliminar cliente de la lista
            clients.forEach {
                if (it.first == socket) {
                    clients.remove(it)
                }
            }
            socket.close()
        }
    }

    suspend fun sendToAllClients(message: String, socketInterface: SocketInterface) {
        try {
            println("Aliiii send all client: ${clients}")
            clients.forEach { socket ->
//            val output = socket.openWriteChannel(autoFlush = true)
                socket.second?.writeStringUtf8("$message\n")
                socketInterface.messageListener(Pair(message, 2))
            }
        } catch (e: Exception) {
            e.printStack()
        }
    }

    companion object {
        private val instanceClass: SocketServer by lazy { SocketServer() }

        fun getInstance(): SocketServer {
            return instanceClass
        }
    }
}
