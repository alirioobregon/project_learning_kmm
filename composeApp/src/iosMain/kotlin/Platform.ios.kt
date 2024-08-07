import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val ip: String
        get() = ""
}

actual fun getPlatform(): Platform = IOSPlatform()