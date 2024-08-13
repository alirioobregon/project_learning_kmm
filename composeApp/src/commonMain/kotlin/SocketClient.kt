import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.isClosed
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeStringUtf8
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import models.Messages
import utils.SerializationData

class SocketClient {
    private val port = 12342
    private var socket: Socket? = null
    private var channelOutput: ByteWriteChannel? = null
    val msg = "{asasas}"


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
                        println("Received from server 1: $response")
                        val message = SerializationData.getInstance().deserializeMessage(response)
                        socketInterface.messageListener(message)

                        // Coroutine para manejar la recepción de mensajes
                        launch {
                            while (true) {
                                val serverMessage = input?.readUTF8Line(limit = 16000)
                                println("Received from server 2: ${serverMessage.toString()}")
                                if (serverMessage != null) {
                                    println("Received from server 3: $serverMessage")
                                    val message = SerializationData.getInstance()
                                        .deserializeMessage(serverMessage)
                                    socketInterface.messageListener(message)
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
                println("error Connection failed: ${e.message}")
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
                    println("Aliiiii error conexion client $response")
                    if (!response.isNullOrEmpty()) {
                        return@withTimeoutOrNull response
                    }
                    delay(100) // Esperar un poco antes de intentar nuevamente
                }
                null // Timeout alcanzado sin respuesta
            }
        } catch (e: Exception) {
            println("Aliiiii wait error conexion client $e")
            return null
        }
    }

    suspend fun sendToServer(message: String, socketInterface: SocketInterface) {
        try {
            if (channelOutput == null) {
                channelOutput = socket?.openWriteChannel(autoFlush = true)
            }
            val messagesToSend = Messages(2, message, 2)
            val serializedMessage = SerializationData.getInstance().serializeMessage(messagesToSend)
            channelOutput?.writeStringUtf8("$serializedMessage\n")
            println(
                "Aliii send to server $messagesToSend --- $socket --- $channelOutput -- ${
                    SerializationData.getInstance().serializeMessage(messagesToSend)
                }"
            )
            socketInterface.messageListener(messagesToSend)
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

