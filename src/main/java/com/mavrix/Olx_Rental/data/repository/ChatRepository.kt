package com.mavrix.Olx_Rental.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mavrix.Olx_Rental.data.model.ChatMessage
import com.mavrix.Olx_Rental.data.model.ChatRoom
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date

class ChatRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun getChatRooms(userId: String): Flow<List<ChatRoom>> = callbackFlow {
        val listener = firestore.collection("chats")
            .whereArrayContains("participants", userId)
            .orderBy("lastMessageTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ChatRepository", "Listen error", error)
                    return@addSnapshotListener
                }

                val chatRooms = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        ChatRoom.fromMap(doc.data ?: return@mapNotNull null, doc.id)
                    } catch (e: Exception) {
                        Log.e("ChatRepository", "Parse error", e)
                        null
                    }
                } ?: emptyList()

                trySend(chatRooms)
            }

        awaitClose { listener.remove() }
    }

    fun getMessages(chatId: String): Flow<List<ChatMessage>> = callbackFlow {
        val listener = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ChatRepository", "Listen error", error)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        ChatMessage.fromMap(doc.data ?: return@mapNotNull null, doc.id)
                    } catch (e: Exception) {
                        Log.e("ChatRepository", "Parse error", e)
                        null
                    }
                } ?: emptyList()

                trySend(messages)
            }

        awaitClose { listener.remove() }
    }

    suspend fun createOrGetChatRoom(userId: String, otherUserId: String, listingId: String): Result<String> {
        return try {
            val participants = listOf(userId, otherUserId).sorted()
            val chatId = "${participants[0]}_${participants[1]}_$listingId"

            val doc = firestore.collection("chats").document(chatId).get().await()

            if (!doc.exists()) {
                val chatRoom = ChatRoom(
                    id = chatId,
                    participants = participants,
                    listingId = listingId,
                    lastMessage = "",
                    lastMessageTime = Date(),
                    createdAt = Date()
                )
                firestore.collection("chats").document(chatId).set(chatRoom.toMap()).await()
            }

            Result.success(chatId)
        } catch (e: Exception) {
            Log.e("ChatRepository", "Create chat room error", e)
            Result.failure(e)
        }
    }

    suspend fun sendMessage(chatId: String, senderId: String, message: String): Result<Unit> {
        return try {
            val chatMessage = ChatMessage(
                senderId = senderId,
                message = message,
                timestamp = Date()
            )

            firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(chatMessage.toMap())
                .await()

            firestore.collection("chats")
                .document(chatId)
                .update(
                    mapOf(
                        "lastMessage" to message,
                        "lastMessageTime" to com.google.firebase.Timestamp(Date())
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ChatRepository", "Send message error", e)
            Result.failure(e)
        }
    }
}
