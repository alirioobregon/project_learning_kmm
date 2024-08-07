class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }

    fun getIp(): String {
        return "Tu ip es: ${platform.ip}"
    }

}