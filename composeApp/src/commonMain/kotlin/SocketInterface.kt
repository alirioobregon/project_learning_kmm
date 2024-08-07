interface SocketInterface {
    fun successServerConnect()
    fun errorServerConnect()
    fun successClientConnect()

    fun messageListener(message: Pair<String, Int>)
}