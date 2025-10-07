package com.mavrix.Olx_Rental.data.model

import com.google.firebase.Timestamp
import java.util.Date

data class ListingModel(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val category: ListingCategory = ListingCategory.OTHER,
    val type: ListingType = ListingType.PRODUCT,
    val images: List<String> = emptyList(),
    val location: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val condition: ListingCondition? = null,
    val isActive: Boolean = true,
    val createdAt: Date = Date(),
    val updatedAt: Date? = null,
    val rentalDetails: Map<String, Any>? = null,
    val laborDetails: Map<String, Any>? = null,
    val views: Int = 0,
    val savedBy: List<String> = emptyList()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "userId" to userId,
        "title" to title,
        "description" to description,
        "price" to price,
        "category" to category.name.lowercase(),
        "type" to type.name.lowercase(),
        "images" to images,
        "location" to location,
        "latitude" to latitude,
        "longitude" to longitude,
        "condition" to condition?.name?.lowercase(),
        "isActive" to isActive,
        "createdAt" to Timestamp(createdAt),
        "updatedAt" to updatedAt?.let { Timestamp(it) },
        "rentalDetails" to rentalDetails,
        "laborDetails" to laborDetails,
        "views" to views,
        "savedBy" to savedBy
    )

    companion object {
        fun fromMap(map: Map<String, Any>, id: String): ListingModel {
            return ListingModel(
                id = id,
                userId = map["userId"] as? String ?: "",
                title = map["title"] as? String ?: "",
                description = map["description"] as? String ?: "",
                price = (map["price"] as? Number)?.toDouble() ?: 0.0,
                category = ListingCategory.fromString(map["category"] as? String ?: "other"),
                type = ListingType.fromString(map["type"] as? String ?: "product"),
                images = (map["images"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList(),
                location = map["location"] as? String ?: "",
                latitude = (map["latitude"] as? Number)?.toDouble(),
                longitude = (map["longitude"] as? Number)?.toDouble(),
                condition = (map["condition"] as? String)?.let { ListingCondition.fromString(it) },
                isActive = map["isActive"] as? Boolean ?: true,
                createdAt = (map["createdAt"] as? Timestamp)?.toDate() ?: Date(),
                updatedAt = (map["updatedAt"] as? Timestamp)?.toDate(),
                rentalDetails = map["rentalDetails"] as? Map<String, Any>,
                laborDetails = map["laborDetails"] as? Map<String, Any>,
                views = (map["views"] as? Number)?.toInt() ?: 0,
                savedBy = (map["savedBy"] as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
            )
        }
    }
}

enum class ListingCategory {
    ELECTRONICS, VEHICLES, REAL_ESTATE, FURNITURE, FASHION, SERVICES, OTHER;

    companion object {
        fun fromString(value: String): ListingCategory {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: OTHER
        }
    }
}

enum class ListingType {
    PRODUCT, RENTAL, LABOR;

    companion object {
        fun fromString(value: String): ListingType {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: PRODUCT
        }
    }
}

enum class ListingCondition {
    BRAND_NEW, LIKE_NEW, GOOD, FAIR, POOR;

    companion object {
        fun fromString(value: String): ListingCondition {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: GOOD
        }
    }
}
