import models.Messages

interface SocketInterface {
    fun successServerConnect()
    fun errorServerConnect()
    fun successClientConnect()

    fun messageListener(messages: Messages)
}