package com.mavrix.Olx_Rental.data.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import java.util.Locale

class LocationService(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    
    private val geocoder = Geocoder(context, Locale.getDefault())

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Result<LocationData> {
        return try {
            if (!hasLocationPermission()) {
                return Result.failure(Exception("Location permission not granted"))
            }

            val cancellationTokenSource = CancellationTokenSource()
            
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await()

            if (location != null) {
                val address = getAddressFromCoordinates(location.latitude, location.longitude)
                
                Result.success(
                    LocationData(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        address = address ?: "Unknown location"
                    )
                )
            } else {
                Result.failure(Exception("Unable to get location"))
            }
        } catch (e: Exception) {
            Log.e("LocationService", "Error getting location", e)
            Result.failure(e)
        }
    }

    private fun getAddressFromCoordinates(latitude: Double, longitude: Double): String? {
        return try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val addressParts = mutableListOf<String>()
                
                address.subLocality?.let { addressParts.add(it) }
                address.locality?.let { addressParts.add(it) }
                address.adminArea?.let { addressParts.add(it) }
                
                addressParts.joinToString(", ")
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("LocationService", "Error getting address", e)
            null
        }
    }

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getDistanceInKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val earthRadius = 6371.0 // km
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        
        return earthRadius * c
    }

    fun formatDistance(distanceInKm: Double): String {
        return when {
            distanceInKm < 1 -> "${(distanceInKm * 1000).toInt()} m"
            distanceInKm < 10 -> String.format("%.1f km", distanceInKm)
            else -> "${distanceInKm.toInt()} km"
        }
    }
}

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val address: String
)
