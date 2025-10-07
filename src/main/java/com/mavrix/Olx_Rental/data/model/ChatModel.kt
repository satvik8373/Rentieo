package com.mavrix.Olx_Rental.data.model

import com.google.firebase.Timestamp
import java.util.Date

data class ChatRoom(
    val id: String = "",
    val participants: List<String> = emptyList(),
    val listingId: String = "",
    val lastMessage: String = "",
    val lastMessageTime: Date = Date(),
    val createdAt: Date = Date()
) {
    fun toMap(): Map<String, Any> = mapOf(
        "participants" to participants,
        "listingId" to listingId,
        "lastMessage" to lastMessage,
        "lastMessageTime" to Timestamp(lastMessageTime),
        "createdAt" to Timestamp(createdAt)
    )

    companion object {
        fun fromMap(map: Map<String, Any>, id: String): ChatRoom {
            return ChatRoom(
                id = id,
                participants = (map["participants"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                listingId = map["listingId"] as? String ?: "",
                lastMessage = map["lastMessage"] as? String ?: "",
                lastMessageTime = (map["lastMessageTime"] as? Timestamp)?.toDate() ?: Date(),
                createdAt = (map["createdAt"] as? Timestamp)?.toDate() ?: Date()
            )
        }
    }
}

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val message: String = "",
    val timestamp: Date = Date(),
    val type: MessageType = MessageType.TEXT
) {
    fun toMap(): Map<String, Any> = mapOf(
        "senderId" to senderId,
        "message" to message,
        "timestamp" to Timestamp(timestamp),
        "type" to type.name.lowercase()
    )

    companion object {
        fun fromMap(map: Map<String, Any>, id: String): ChatMessage {
            return ChatMessage(
                id = id,
                senderId = map["senderId"] as? String ?: "",
                message = map["message"] as? String ?: "",
                timestamp = (map["timestamp"] as? Timestamp)?.toDate() ?: Date(),
                type = MessageType.fromString(map["type"] as? String ?: "text")
            )
        }
    }
}

enum class MessageType {
    TEXT, IMAGE, VIDEO;

    companion object {
        fun fromString(value: String): MessageType {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: TEXT
        }
    }
}
