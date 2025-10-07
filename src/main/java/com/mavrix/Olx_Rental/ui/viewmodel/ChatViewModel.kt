package com.mavrix.Olx_Rental.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mavrix.Olx_Rental.data.model.ChatMessage
import com.mavrix.Olx_Rental.data.model.ChatRoom
import com.mavrix.Olx_Rental.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val repository = ChatRepository()

    private val _chatRooms = MutableStateFlow<List<ChatRoom>>(emptyList())
    val chatRooms: StateFlow<List<ChatRoom>> = _chatRooms.asStateFlow()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadChatRooms(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getChatRooms(userId).collect { rooms ->
                _chatRooms.value = rooms
                _isLoading.value = false
            }
        }
    }

    fun loadMessages(chatId: String) {
        viewModelScope.launch {
            repository.getMessages(chatId).collect { msgs ->
                _messages.value = msgs
            }
        }
    }

    fun createOrGetChatRoom(
        userId: String,
        otherUserId: String,
        listingId: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            repository.createOrGetChatRoom(userId, otherUserId, listingId)
                .onSuccess { chatId -> onSuccess(chatId) }
                .onFailure { error -> onError(error.message ?: "Failed to create chat") }
        }
    }

    fun sendMessage(chatId: String, senderId: String, message: String) {
        viewModelScope.launch {
            repository.sendMessage(chatId, senderId, message)
        }
    }
}
