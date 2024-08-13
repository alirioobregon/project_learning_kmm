package utils

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Messages

class SerializationData {

    private val json = Json { prettyPrint = false }

    fun serializeMessage(messages: Messages): String {
        return json.encodeToString(messages)
    }

    fun deserializeMessage(jsonString: String): Messages {
        return json.decodeFromString(jsonString)
    }

    companion object {

        private val instanceClass: SerializationData by lazy { SerializationData() }

        fun getInstance(): SerializationData {
            return instanceClass
        }

    }

}