import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import com.ali.examplefirst.MainApplication
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Locale

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val ip: String get() = getLocalIpAddress()
}

actual fun getPlatform(): Platform = AndroidPlatform()

fun getLocalIpAddress(): String {
    try {
        val wifiManager =
            MainApplication.instance.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        val ip = wifiInfo.ipAddress

        val formattedIpAddress = String.format(
            Locale.getDefault(),
            "%d.%d.%d.%d",
            (ip and 0xff),
            (ip shr 8 and 0xff),
            (ip shr 16 and 0xff),
            (ip shr 24 and 0xff)
        )

        if (formattedIpAddress != "0.0.0.0") {
            return formattedIpAddress
        }

        val en = NetworkInterface.getNetworkInterfaces()
        while (en.hasMoreElements()) {
            val networkInterface = en.nextElement()
            val enumIpAddr = networkInterface.inetAddresses
            while (enumIpAddr.hasMoreElements()) {
                val inetAddress = enumIpAddr.nextElement()
                if (!inetAddress.isLoopbackAddress && inetAddress is InetAddress) {
                    println("Alii ip ${inetAddress.hostAddress}")
                    return inetAddress.hostAddress?.toString() ?: ""
                }
            }
        }
        return "No IP Address Found"
    } catch (ex: Exception) {
        ex.printStackTrace()
        return ex.toString()
    }

//    return "No IP Address Found"
}