package com.mavrix.Olx_Rental.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mavrix.Olx_Rental.data.model.UserModel
import com.mavrix.Olx_Rental.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _currentUser = MutableStateFlow<UserModel?>(null)
    val currentUser: StateFlow<UserModel?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    val isAuthenticated: Boolean
        get() = _currentUser.value != null

    init {
        initializeAuth()
    }

    private fun initializeAuth() {
        viewModelScope.launch {
            android.util.Log.d("AuthViewModel", "🔐 Initializing AuthViewModel...")
            
            try {
                val user = repository.currentUser
                if (user != null) {
                    android.util.Log.d("AuthViewModel", "✅ User already signed in: ${user.uid}")
                    _currentUser.value = repository.getUserData(user.uid)
                } else {
                    android.util.Log.d("AuthViewModel", "❌ No user signed in")
                }
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "❌ Error during auth initialization: ${e.message}", e)
            } finally {
                _isInitialized.value = true
                android.util.Log.d("AuthViewModel", "✅ AuthViewModel initialized. Authenticated: $isAuthenticated")
            }
        }
    }

    fun signInWithEmail(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            android.util.Log.d("AuthViewModel", "📧 Attempting sign in with email: $email")
            
            repository.signInWithEmail(email, password)
                .onSuccess { user ->
                    android.util.Log.d("AuthViewModel", "✅ Sign in successful: ${user.name}")
                    _currentUser.value = user
                    _errorMessage.value = null
                    onSuccess()
                }
                .onFailure { error ->
                    android.util.Log.e("AuthViewModel", "❌ Sign in failed: ${error.message}")
                    _errorMessage.value = error.message ?: "Login failed"
                }
            
            _isLoading.value = false
        }
    }

    fun signUpWithEmail(email: String, password: String, name: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            android.util.Log.d("AuthViewModel", "📝 Attempting sign up with email: $email")
            
            repository.signUpWithEmail(email, password, name)
                .onSuccess { user ->
                    android.util.Log.d("AuthViewModel", "✅ Sign up successful: ${user.name}")
                    _currentUser.value = user
                    _errorMessage.value = null
                    onSuccess()
                }
                .onFailure { error ->
                    android.util.Log.e("AuthViewModel", "❌ Sign up failed: ${error.message}")
                    _errorMessage.value = error.message ?: "Sign up failed"
                }
            
            _isLoading.value = false
        }
    }

    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.signInWithGoogle(idToken)
                .onSuccess { user ->
                    _currentUser.value = user
                    onSuccess()
                }
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "Google sign in failed"
                }
            
            _isLoading.value = false
        }
    }

    fun signInAsGuest(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            repository.signInAsGuest()
                .onSuccess { user ->
                    _currentUser.value = user
                    onSuccess()
                }
                .onFailure { error ->
                    _errorMessage.value = error.message ?: "Guest sign in failed"
                }
            
            _isLoading.value = false
        }
    }

    fun signOut() {
        repository.signOut()
        _currentUser.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    suspend fun getUserById(userId: String): UserModel? {
        return repository.getUserData(userId)
    }

    suspend fun updateProfile(updates: Map<String, Any>) {
        repository.updateProfile(updates)
        currentUser.value?.let { user ->
            _currentUser.value = repository.getUserData(user.id)
        }
    }

    private val _allUsers = MutableStateFlow<List<UserModel>>(emptyList())
    val allUsers: StateFlow<List<UserModel>> = _allUsers.asStateFlow()

    fun loadAllUsers() {
        viewModelScope.launch {
            try {
                val usersSnapshot = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("users")
                    .get()
                    .await()
                
                val users = usersSnapshot.documents.mapNotNull { doc ->
                    try {
                        UserModel.fromMap(doc.data ?: emptyMap(), doc.id)
                    } catch (e: Exception) {
                        null
                    }
                }
                _allUsers.value = users
            } catch (e: Exception) {
                _allUsers.value = emptyList()
            }
        }
    }
    
    // ========== Admin Functions ==========
    
    fun toggleUserVerification(userId: String, isVerified: Boolean) {
        viewModelScope.launch {
            try {
                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .update("isVerified", isVerified)
                    .await()
                loadAllUsers() // Refresh list
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "Error toggling verification: ${e.message}")
            }
        }
    }
    
    fun toggleAdminRole(userId: String, currentRole: com.mavrix.Olx_Rental.data.model.UserRole) {
        viewModelScope.launch {
            try {
                val newRole = if (currentRole == com.mavrix.Olx_Rental.data.model.UserRole.ADMIN) {
                    com.mavrix.Olx_Rental.data.model.UserRole.BUYER
                } else {
                    com.mavrix.Olx_Rental.data.model.UserRole.ADMIN
                }
                
                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .update("role", newRole.name.lowercase())
                    .await()
                loadAllUsers() // Refresh list
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "Error toggling admin role: ${e.message}")
            }
        }
    }
    
    fun deleteUser(userId: String) {
        viewModelScope.launch {
            try {
                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .delete()
                    .await()
                loadAllUsers() // Refresh list
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "Error deleting user: ${e.message}")
            }
        }
    }
}
