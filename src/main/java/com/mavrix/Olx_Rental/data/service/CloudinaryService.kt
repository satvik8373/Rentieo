package com.mavrix.Olx_Rental.data.service

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class CloudinaryService(private val context: Context) {
    
    private val cloudName = "dxucmkcy9"
    private val uploadPreset = "olx_storage"
    private val client = OkHttpClient()

    suspend fun uploadImage(uri: Uri, folder: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d("CloudinaryService", "Starting upload for: $uri")
            
            // Convert URI to File
            val file = uriToFile(uri) ?: return@withContext Result.failure(Exception("Failed to convert URI to file"))
            
            // Create multipart request
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
                .addFormDataPart("upload_preset", uploadPreset)
                .addFormDataPart("folder", folder)
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val json = JSONObject(responseBody)
                val secureUrl = json.getString("secure_url")
                Log.d("CloudinaryService", "Upload successful: $secureUrl")
                
                // Clean up temp file
                file.delete()
                
                Result.success(secureUrl)
            } else {
                Log.e("CloudinaryService", "Upload failed: ${response.code} - $responseBody")
                Result.failure(Exception("Upload failed: ${response.code}"))
            }
        } catch (e: Exception) {
            Log.e("CloudinaryService", "Upload error", e)
            Result.failure(e)
        }
    }

    suspend fun uploadImages(uris: List<Uri>, folder: String): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val urls = mutableListOf<String>()
            
            for (uri in uris) {
                val result = uploadImage(uri, folder)
                result.onSuccess { url ->
                    urls.add(url)
                }.onFailure { error ->
                    Log.e("CloudinaryService", "Failed to upload image: $uri", error)
                }
            }
            
            if (urls.isNotEmpty()) {
                Result.success(urls)
            } else {
                Result.failure(Exception("No images uploaded successfully"))
            }
        } catch (e: Exception) {
            Log.e("CloudinaryService", "Batch upload error", e)
            Result.failure(e)
        }
    }

    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            
            FileOutputStream(tempFile).use { output ->
                inputStream.copyTo(output)
            }
            
            tempFile
        } catch (e: Exception) {
            Log.e("CloudinaryService", "Error converting URI to file", e)
            null
        }
    }
}
