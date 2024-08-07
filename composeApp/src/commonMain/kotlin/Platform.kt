interface Platform {
    val name: String
    val ip: String
}

expect fun getPlatform(): Platform