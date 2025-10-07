package com.mavrix.Olx_Rental.data.service

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class StorageService(private val context: Context) {
    
    private val storage = FirebaseStorage.getInstance()

    suspend fun uploadImage(uri: Uri, folder: String): Result<String> {
        return try {
            val fileName = "${System.currentTimeMillis()}_${uri.lastPathSegment}"
            val storageRef = storage.reference.child("$folder/$fileName")
            
            Log.d("StorageService", "Uploading to: $folder/$fileName")
            
            val uploadTask = storageRef.putFile(uri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()
            
            Log.d("StorageService", "Upload successful: $downloadUrl")
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Log.e("StorageService", "Upload error", e)
            Result.failure(e)
        }
    }

    suspend fun uploadImages(uris: List<Uri>, folder: String): Result<List<String>> {
        return try {
            val urls = mutableListOf<String>()
            
            for (uri in uris) {
                val result = uploadImage(uri, folder)
                result.onSuccess { url ->
                    urls.add(url)
                }.onFailure { error ->
                    Log.e("StorageService", "Failed to upload image: $uri", error)
                }
            }
            
            if (urls.isNotEmpty()) {
                Result.success(urls)
            } else {
                Result.failure(Exception("No images uploaded successfully"))
            }
        } catch (e: Exception) {
            Log.e("StorageService", "Batch upload error", e)
            Result.failure(e)
        }
    }

    suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return try {
            val storageRef = storage.getReferenceFromUrl(imageUrl)
            storageRef.delete().await()
            
            Log.d("StorageService", "Image deleted successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("StorageService", "Delete error", e)
            Result.failure(e)
        }
    }

    suspend fun deleteImages(imageUrls: List<String>): Result<Unit> {
        return try {
            for (url in imageUrls) {
                deleteImage(url)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("StorageService", "Batch delete error", e)
            Result.failure(e)
        }
    }
}
