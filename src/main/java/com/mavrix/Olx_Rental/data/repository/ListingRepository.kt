package com.mavrix.Olx_Rental.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mavrix.Olx_Rental.data.model.ListingModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ListingRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun getListings(): Flow<List<ListingModel>> = callbackFlow {
        val listener = firestore.collection("listings")
            .whereEqualTo("isActive", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ListingRepository", "Listen error", error)
                    return@addSnapshotListener
                }
                
                val listings = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        ListingModel.fromMap(doc.data ?: return@mapNotNull null, doc.id)
                    } catch (e: Exception) {
                        Log.e("ListingRepository", "Parse error", e)
                        null
                    }
                } ?: emptyList()
                
                trySend(listings)
            }
        
        awaitClose { listener.remove() }
    }

    fun getAllListingsForAdmin(): Flow<List<ListingModel>> = callbackFlow {
        val listener = firestore.collection("listings")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ListingRepository", "Listen error", error)
                    return@addSnapshotListener
                }
                
                val listings = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        ListingModel.fromMap(doc.data ?: return@mapNotNull null, doc.id)
                    } catch (e: Exception) {
                        Log.e("ListingRepository", "Parse error", e)
                        null
                    }
                } ?: emptyList()
                
                trySend(listings)
            }
        
        awaitClose { listener.remove() }
    }

    fun getUserListings(userId: String): Flow<List<ListingModel>> = callbackFlow {
        val listener = firestore.collection("listings")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ListingRepository", "Listen error", error)
                    return@addSnapshotListener
                }
                
                val listings = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        ListingModel.fromMap(doc.data ?: return@mapNotNull null, doc.id)
                    } catch (e: Exception) {
                        Log.e("ListingRepository", "Parse error", e)
                        null
                    }
                } ?: emptyList()
                
                trySend(listings)
            }
        
        awaitClose { listener.remove() }
    }

    suspend fun createListing(listing: ListingModel): Result<String> {
        return try {
            val docRef = firestore.collection("listings").add(listing.toMap()).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("ListingRepository", "Create listing error", e)
            Result.failure(e)
        }
    }

    suspend fun updateListing(listingId: String, data: Map<String, Any>): Result<Unit> {
        return try {
            firestore.collection("listings").document(listingId).update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ListingRepository", "Update listing error", e)
            Result.failure(e)
        }
    }

    suspend fun deleteListing(listingId: String): Result<Unit> {
        return try {
            firestore.collection("listings").document(listingId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ListingRepository", "Delete listing error", e)
            Result.failure(e)
        }
    }

    suspend fun getListing(listingId: String): Result<ListingModel> {
        return try {
            val doc = firestore.collection("listings").document(listingId).get().await()
            if (doc.exists()) {
                val listing = ListingModel.fromMap(doc.data!!, doc.id)
                Result.success(listing)
            } else {
                Result.failure(Exception("Listing not found"))
            }
        } catch (e: Exception) {
            Log.e("ListingRepository", "Get listing error", e)
            Result.failure(e)
        }
    }
}
