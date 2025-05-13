import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.isClosed
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import models.Messages
import utils.SerializationData

class SocketServer {
    private val port = 12342
    private var serverSocket: ServerSocket? = null
    private val clients = mutableSetOf<Pair<Socket, ByteWriteChannel?>>()


    suspend fun startServer(socketInterface: SocketInterface, ip: String) {
        try {
            withContext(Dispatchers.IO) {
                val selector = SelectorManager(Dispatchers.Default)
                serverSocket = aSocket(selector).tcp().bind(port = port)
                println("Aliii Server started on port $ip $port --- ${serverSocket?.localAddress}")

                serverSocket?.let {
                    if (!it.isClosed) {
                        socketInterface.successServerConnect()
                    } else {
                        socketInterface.errorServerConnect()
                    }

                    while (true) {
                        val socket = it.accept()
                        println("Aliii Server acepted ${socket.localAddress} --- ${socket.remoteAddress}")
                        launch {
                            handleClient(socket, socketInterface)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            socketInterface.errorServerConnect()
        }
    }

    private suspend fun handleClient(socket: Socket, socketInterface: SocketInterface) {
        try {

            val input = socket.openReadChannel()
            val output = socket.openWriteChannel(autoFlush = true)

            val pair = Pair(socket, output)
            clients.add(pair)

            // sent first response to client
            val messages = Messages(1, "First execution", true)
//            output.writeStringUtf8("First execution\n")
            val serializedMessage = SerializationData.getInstance().serializeMessage(messages)
            println("Sending to client: $serializedMessage")
            output.writeStringUtf8("$serializedMessage\n")
            socketInterface.messageListener(messages)


            while (true) {
                val clientMessage = input.readUTF8Line(limit = 16000)
                println("Aliiii Received from client 1: ${clientMessage.toString()}")
                if (clientMessage != null) {
                    val message = SerializationData.getInstance().deserializeMessage(clientMessage)
                    println("Aliiii Received from client 2: $message")
                    message.transmitter = false
                    socketInterface.messageListener(message)
                } else {
                    // Si el cliente envía null, significa que se desconectó
                    break
                }
            }
//            }
        } catch (e: Exception) {
            e.printStackTrace()
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
            println("Aliiii send all client: $clients")
            clients.forEach { socket ->
//            val output = socket.openWriteChannel(autoFlush = true)
                val messagesLast = Messages(1, message, true)
                val serializedMessage =
                    SerializationData.getInstance().serializeMessage(messagesLast)
                clients.forEach { (_, output) ->
                    output?.writeStringUtf8("$serializedMessage\n")
                }
                socketInterface.messageListener(messagesLast)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            serverSocket = null
            clients.clear()
            socketInterface.errorServerConnect()
        }
    }

    fun closeSocket(socketInterface: SocketInterface) {
        try {
            serverSocket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        serverSocket = null
        clients.clear()
        socketInterface.errorServerConnect()

    }

    companion object {
        private val instanceClass: SocketServer by lazy { SocketServer() }

        fun getInstance(): SocketServer {
            return instanceClass
        }
    }
}
