package com.mavrix.Olx_Rental.data.model

import com.google.firebase.Timestamp
import java.util.Date

data class UserModel(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val phone: String? = null,
    val photoUrl: String? = null,
    val bio: String? = null,
    val role: UserRole = UserRole.BUYER,
    val isVerified: Boolean = false,
    val rating: Double = 0.0,
    val totalRatings: Int = 0,
    val createdAt: Date = Date(),
    val skills: List<String>? = null,
    val availability: Map<String, Any>? = null
) {
    val isAdmin: Boolean
        get() = role == UserRole.ADMIN

    fun toMap(): Map<String, Any?> = mapOf(
        "email" to email,
        "name" to name,
        "phone" to phone,
        "photoUrl" to photoUrl,
        "bio" to bio,
        "role" to role.name.lowercase(),
        "isVerified" to isVerified,
        "rating" to rating,
        "totalRatings" to totalRatings,
        "createdAt" to Timestamp(createdAt),
        "skills" to skills,
        "availability" to availability
    )

    companion object {
        fun fromMap(map: Map<String, Any>, id: String): UserModel {
            return UserModel(
                id = id,
                email = map["email"] as? String ?: "",
                name = map["name"] as? String ?: "",
                phone = map["phone"] as? String,
                photoUrl = map["photoUrl"] as? String,
                bio = map["bio"] as? String,
                role = UserRole.fromString(map["role"] as? String ?: "buyer"),
                isVerified = map["isVerified"] as? Boolean ?: false,
                rating = (map["rating"] as? Number)?.toDouble() ?: 0.0,
                totalRatings = (map["totalRatings"] as? Number)?.toInt() ?: 0,
                createdAt = (map["createdAt"] as? Timestamp)?.toDate() ?: Date(),
                skills = (map["skills"] as? List<*>)?.mapNotNull { it as? String },
                availability = map["availability"] as? Map<String, Any>
            )
        }
    }
}

enum class UserRole {
    BUYER, SELLER, RENTAL, LABOR, ADMIN;

    companion object {
        fun fromString(value: String): UserRole {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: BUYER
        }
    }
}
