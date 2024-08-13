import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.isClosed
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.util.KtorExperimentalAPI
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.printStack
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class SocketClient {
    private val port = 12342
    private var socket: Socket? = null
    private var channelOutput: ByteWriteChannel? = null

    suspend fun connectToServer2(serverIp: String, socketInterface: SocketInterface): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val selector = SelectorManager(Dispatchers.IO)
                socket = try {
                    aSocket(selector).tcp().connect(serverIp, port)
                } catch (e: Exception) {
                    println("Aliii Failed to connect: ${e.message}")
                    null
                }

                if (socket?.isClosed == false) {


                    val input = socket?.openReadChannel()
                    println("Aliii data server conected ${socket?.remoteAddress} ---- ${socket?.localAddress} -- $input")
//                    val output = socket?.openWriteChannel(autoFlush = true)

                    // Enviar mensaje al servidor
                    val messageToSend = "Test!"
//                    output?.writeStringUtf8("$messageToSend\n")
                    sendToServer(messageToSend, socketInterface)

                    // Intentar leer la respuesta del servidor con un timeout
                    val response = waitForResponse(input)

                    return@withContext if (response != null) {
                        println("Received from server: $response")
                        socketInterface.messageListener(Pair(response, 1))

                        // Coroutine para manejar la recepción de mensajes
                        launch {
                            while (true) {
                                val serverMessage = input?.readUTF8Line(limit = 16000)
                                println("Received from server: ${serverMessage.toString()}")
                                if (serverMessage != null) {
                                    println("Received from server: $serverMessage")
                                    socketInterface.messageListener(Pair(serverMessage, 1))
                                }
                            }
                        }
                        socketInterface.successClientConnect()

                        true // Conexión exitosa
                    } else {
                        println("No response from server, connection might have failed.")
                        socketInterface.errorServerConnect()
                        false // Fallo en la conexión
                    }


                } else {
                    socketInterface.errorServerConnect()
                    return@withContext false
                }

            } catch (e: Exception) {
                println("Connection failed: ${e.message}")
                socketInterface.errorServerConnect()
                return@withContext false
            }
        }
    }

    private suspend fun waitForResponse(input: ByteReadChannel?): String? {
        try {
            return withTimeoutOrNull(60000) { // Timeout de 5 segundos
                while (true) {
                    val response = input?.readUTF8Line(limit = 16000)
                    println("Aliiiii error conexion $response")
                    if (!response.isNullOrEmpty()) {
                        return@withTimeoutOrNull response
                    }
                    delay(100) // Esperar un poco antes de intentar nuevamente
                }
                null // Timeout alcanzado sin respuesta
            }
        } catch (e: Exception) {
            println("Aliiiii error conexion $e")
            return null
        }
    }

    suspend fun connectToServer(serverIp: String, socketInterface: SocketInterface) {
        try {
            withContext(Dispatchers.IO) {
                val selector = SelectorManager(Dispatchers.Default)
                println("Aliii data server conecting...")
                socket = aSocket(selector).tcp().connect(serverIp, port)


                if (socket?.isClosed == false) {
                    println("Aliii data server conected ${socket?.remoteAddress} ---- ${socket?.localAddress}")
                    println("Aliii data server conected is $")
                    socketInterface.successClientConnect()
                }

                val input = socket?.openReadChannel()
//            val output = socket?.openWriteChannel(autoFlush = true)

                // Coroutine para manejar la recepción de mensajes
                launch {
                    while (true) {
                        val serverMessage = input?.readUTF8Line(limit = 1024)
                        if (serverMessage != null) {
                            println("Received from server: $serverMessage")
                            socketInterface.messageListener(Pair(serverMessage, 1))
                        }
                    }
                }

                // Enviar mensaje al servidor
                sendToServer("Test!", socketInterface)
            }
        } catch (e: Exception) {
            println("Connection failed: ${e}")
        }
    }

    suspend fun sendToServer(message: String, socketInterface: SocketInterface) {
        try {
            if (channelOutput == null) {
                channelOutput = socket?.openWriteChannel(autoFlush = true)
            }
            channelOutput?.writeStringUtf8("$message\n")
            println("Aliii send to server $message --- $socket --- $channelOutput")
            socketInterface.messageListener(Pair(message, 2))
        } catch (e: Exception) {
            println("Aliii send to server error: $e")
        }
    }

    companion object {
        private val instanceClass: SocketClient by lazy { SocketClient() }

        fun getInstance(): SocketClient {
            return instanceClass
        }
    }
}

