package com.mavrix.Olx_Rental.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mavrix.Olx_Rental.data.model.UserModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.util.Date

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val currentUser get() = auth.currentUser
    
    companion object {
        private const val TAG = "AuthRepository"
        private const val USERS_COLLECTION = "users"
    }

    suspend fun signInWithEmail(email: String, password: String): Result<UserModel> {
        return try {
            Log.d(TAG, "📧 Attempting sign in: $email")
            
            // Step 1: Authenticate with Firebase Auth
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user 
                ?: return Result.failure(Exception("Authentication failed"))
            
            Log.d(TAG, "✅ Firebase Auth successful: ${firebaseUser.uid}")
            
            // Step 2: Load or create user profile from Firestore
            val userModel = loadOrCreateUserProfile(
                uid = firebaseUser.uid,
                email = firebaseUser.email ?: email,
                name = firebaseUser.displayName,
                photoUrl = firebaseUser.photoUrl?.toString()
            )
            
            Log.d(TAG, "✅ Sign in complete: ${userModel.name}")
            Result.success(userModel)
            
        } catch (e: FirebaseAuthException) {
            Log.e(TAG, "❌ Firebase Auth error: ${e.errorCode}")
            val errorMessage = getAuthErrorMessage(e.errorCode)
            Result.failure(Exception(errorMessage))
        } catch (e: com.google.firebase.FirebaseTooManyRequestsException) {
            Log.e(TAG, "❌ Rate limit exceeded")
            Result.failure(Exception("Too many login attempts. Please wait 5-10 minutes and try again."))
        } catch (e: Exception) {
            Log.e(TAG, "❌ Sign in error: ${e.message}", e)
            // Check if it's a rate limit error
            if (isRateLimitError(e)) {
                Result.failure(Exception("Too many attempts. Please wait a few minutes and try again."))
            } else {
                Result.failure(Exception("Sign in failed. Please try again."))
            }
        }
    }

    suspend fun signUpWithEmail(email: String, password: String, name: String): Result<UserModel> {
        return try {
            Log.d(TAG, "📝 Creating new account: $email")
            
            // Step 1: Create Firebase Auth account
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user 
                ?: return Result.failure(Exception("Account creation failed"))
            
            Log.d(TAG, "✅ Firebase Auth account created: ${firebaseUser.uid}")
            
            // Step 2: Create user profile in Firestore
            val userModel = UserModel(
                id = firebaseUser.uid,
                email = email,
                name = name,
                createdAt = Date(),
                isVerified = false
            )
            
            // Save to Firestore with proper error handling
            val saveResult = saveUserProfile(userModel)
            if (!saveResult) {
                Log.w(TAG, "⚠️ Failed to save profile, but auth account created")
            }
            
            // Step 3: Send verification email (non-blocking)
            try {
                firebaseUser.sendEmailVerification().await()
                Log.d(TAG, "✅ Verification email sent")
            } catch (e: Exception) {
                Log.w(TAG, "⚠️ Could not send verification email: ${e.message}")
            }
            
            Log.d(TAG, "✅ Sign up complete: ${userModel.name}")
            Result.success(userModel)
            
        } catch (e: FirebaseAuthException) {
            Log.e(TAG, "❌ Firebase Auth error: ${e.errorCode}")
            val errorMessage = when (e.errorCode) {
                "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already registered. Please sign in instead."
                "ERROR_INVALID_EMAIL" -> "Invalid email address"
                "ERROR_WEAK_PASSWORD" -> "Password is too weak. Use at least 6 characters"
                "ERROR_OPERATION_NOT_ALLOWED" -> "Email/password sign up is not enabled"
                else -> e.message ?: "Sign up failed"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Log.e(TAG, "❌ Sign up error: ${e.message}", e)
            Result.failure(Exception("Sign up failed. Please try again."))
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<UserModel> {
        return try {
            Log.d("AuthRepository", "🔐 Signing in with Google")
            
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user ?: return Result.failure(Exception("User not found"))
            
            Log.d("AuthRepository", "✅ Google sign in successful: ${user.uid}")
            
            val userDoc = firestore.collection("users").document(user.uid).get().await()
            
            val userModel = if (userDoc.exists()) {
                Log.d("AuthRepository", "✅ Existing user found")
                UserModel.fromMap(userDoc.data!!, user.uid)
            } else {
                Log.d("AuthRepository", "📝 Creating new user profile")
                val newUser = UserModel(
                    id = user.uid,
                    email = user.email ?: "",
                    name = user.displayName ?: "User",
                    photoUrl = user.photoUrl?.toString(),
                    createdAt = Date()
                )
                firestore.collection("users").document(user.uid).set(newUser.toMap()).await()
                newUser
            }
            
            Result.success(userModel)
        } catch (e: com.google.firebase.auth.FirebaseAuthException) {
            Log.e("AuthRepository", "❌ Firebase Auth error: ${e.errorCode} - ${e.message}")
            
            val errorMessage = when (e.errorCode) {
                "ERROR_INVALID_CREDENTIAL" -> "Google Sign-In failed. Please try again or use email/password."
                "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "An account already exists with this email using a different sign-in method."
                "ERROR_CREDENTIAL_ALREADY_IN_USE" -> "This Google account is already linked to another user."
                else -> "Google Sign-In error: ${e.message}"
            }
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Log.e("AuthRepository", "❌ Google sign in error: ${e.message}", e)
            Result.failure(Exception("Google Sign-In failed. Please check your internet connection and try again."))
        }
    }

    suspend fun signInAsGuest(): Result<UserModel> {
        return try {
            val result = auth.signInAnonymously().await()
            val user = result.user ?: return Result.failure(Exception("Guest sign in failed"))
            
            val userModel = UserModel(
                id = user.uid,
                email = "guest@rentieo.com",
                name = "Guest User",
                createdAt = Date()
            )
            
            firestore.collection("users").document(user.uid).set(
                userModel.toMap() + mapOf("isGuest" to true)
            ).await()
            
            Result.success(userModel)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Guest sign in error", e)
            Result.failure(e)
        }
    }

    suspend fun getUserData(uid: String): UserModel {
        return try {
            Log.d("AuthRepository", "📥 Loading user data for: $uid")
            
            val doc = firestore.collection("users").document(uid).get().await()
            
            if (doc.exists()) {
                val userModel = UserModel.fromMap(doc.data!!, uid)
                Log.d("AuthRepository", "✅ User data loaded: ${userModel.name}")
                userModel
            } else {
                Log.w("AuthRepository", "⚠️ User document not found, creating from Firebase Auth data")
                val user = auth.currentUser!!
                val userModel = UserModel(
                    id = uid,
                    email = user.email ?: "",
                    name = user.displayName ?: "User",
                    photoUrl = user.photoUrl?.toString(),
                    createdAt = Date()
                )
                
                // Try to create user document
                try {
                    firestore.collection("users").document(uid).set(userModel.toMap()).await()
                    Log.d("AuthRepository", "✅ User document created")
                } catch (e: Exception) {
                    Log.w("AuthRepository", "⚠️ Could not create user document: ${e.message}")
                }
                
                userModel
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "❌ Error loading user data: ${e.message}", e)
            
            // Even if Firestore fails, try to use Firebase Auth data
            val user = auth.currentUser
            if (user != null) {
                UserModel(
                    id = uid,
                    email = user.email ?: "user@rentieo.com",
                    name = user.displayName ?: "User",
                    photoUrl = user.photoUrl?.toString(),
                    createdAt = Date()
                )
            } else {
                throw Exception("Unable to load user data")
            }
        }
    }

    suspend fun updateProfile(data: Map<String, Any>) {
        try {
            val uid = currentUser?.uid ?: return
            firestore.collection(USERS_COLLECTION).document(uid).update(data).await()
            Log.d(TAG, "✅ Profile updated")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Update profile error: ${e.message}")
        }
    }

    fun signOut() {
        auth.signOut()
        Log.d(TAG, "👋 User signed out")
    }
    
    // ========== Helper Functions ==========
    
    /**
     * Load user profile from Firestore or create if doesn't exist
     * Similar to Flutter's approach
     */
    private suspend fun loadOrCreateUserProfile(
        uid: String,
        email: String,
        name: String?,
        photoUrl: String?
    ): UserModel {
        return try {
            Log.d(TAG, "📥 Loading user profile: $uid")
            
            val doc = firestore.collection(USERS_COLLECTION).document(uid).get().await()
            
            if (doc.exists() && doc.data != null) {
                Log.d(TAG, "✅ Profile found in Firestore")
                UserModel.fromMap(doc.data!!, uid)
            } else {
                Log.d(TAG, "📝 Profile not found, creating new one")
                val newUser = UserModel(
                    id = uid,
                    email = email,
                    name = name ?: "User",
                    photoUrl = photoUrl,
                    createdAt = Date(),
                    isVerified = false
                )
                saveUserProfile(newUser)
                newUser
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error loading profile: ${e.message}")
            // Return basic user model from auth data
            UserModel(
                id = uid,
                email = email,
                name = name ?: "User",
                photoUrl = photoUrl,
                createdAt = Date(),
                isVerified = false
            )
        }
    }
    
    /**
     * Save user profile to Firestore with retry logic
     */
    private suspend fun saveUserProfile(userModel: UserModel): Boolean {
        return try {
            Log.d(TAG, "💾 Saving user profile: ${userModel.id}")
            
            // Use SetOptions.merge() to avoid overwriting existing data
            firestore.collection(USERS_COLLECTION)
                .document(userModel.id)
                .set(userModel.toMap(), SetOptions.merge())
                .await()
            
            // Small delay to ensure Firestore sync
            delay(500)
            
            // Verify save
            val doc = firestore.collection(USERS_COLLECTION).document(userModel.id).get().await()
            if (doc.exists()) {
                Log.d(TAG, "✅ Profile saved successfully")
                true
            } else {
                Log.w(TAG, "⚠️ Profile save verification failed, retrying...")
                // Retry once
                firestore.collection(USERS_COLLECTION)
                    .document(userModel.id)
                    .set(userModel.toMap())
                    .await()
                delay(300)
                Log.d(TAG, "✅ Profile saved on retry")
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to save profile: ${e.message}")
            false
        }
    }
    
    /**
     * Get user-friendly error messages for Firebase Auth errors
     */
    private fun getAuthErrorMessage(errorCode: String?): String {
        return when (errorCode) {
            "ERROR_USER_NOT_FOUND" -> "No account found with this email"
            "ERROR_WRONG_PASSWORD" -> "Incorrect password"
            "ERROR_INVALID_EMAIL" -> "Invalid email address"
            "ERROR_USER_DISABLED" -> "This account has been disabled"
            "ERROR_TOO_MANY_REQUESTS" -> "Too many login attempts. Please wait a few minutes and try again."
            "ERROR_INVALID_CREDENTIAL" -> "Invalid email or password"
            "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Please check your connection"
            else -> "Authentication failed. Please try again"
        }
    }
    
    /**
     * Check if exception is a rate limit error
     */
    private fun isRateLimitError(exception: Exception): Boolean {
        return exception is com.google.firebase.FirebaseTooManyRequestsException ||
               exception.message?.contains("too many requests", ignoreCase = true) == true ||
               exception.message?.contains("unusual activity", ignoreCase = true) == true
    }
}
