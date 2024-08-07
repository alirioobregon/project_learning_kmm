import java.net.InetAddress
import java.net.NetworkInterface

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
    override val ip: String get() = getIpAddress()
}

actual fun getPlatform(): Platform = JVMPlatform()

fun getIpAddress(): String {
    return try {
        val interfaces = NetworkInterface.getNetworkInterfaces().toList()
        for (networkInterface in interfaces) {
            val addresses = networkInterface.inetAddresses.toList()
            for (address in addresses) {
                if (!address.isLoopbackAddress && address is InetAddress) {
                    val ipAddress = address.hostAddress
                    if (ipAddress.contains(".")) { // Verifica que sea una dirección IPv4
                        return ipAddress
                    }
                }
            }
        }
        "No IP Address Found"
    } catch (e: Exception) {
        e.printStackTrace()
        "Error: ${e.message}"
    }
}