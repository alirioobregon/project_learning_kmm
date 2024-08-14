package models

import kotlinx.serialization.Serializable

@Serializable
data class Messages(
    val type: Int,
    val message: String,
    var transmitter: Boolean
)
