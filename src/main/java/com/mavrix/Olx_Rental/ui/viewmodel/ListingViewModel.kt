package com.mavrix.Olx_Rental.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mavrix.Olx_Rental.data.model.ListingModel
import com.mavrix.Olx_Rental.data.repository.ListingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ListingViewModel : ViewModel() {
    private val repository = ListingRepository()

    private val _listings = MutableStateFlow<List<ListingModel>>(emptyList())
    val listings: StateFlow<List<ListingModel>> = _listings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadListings()
    }

    fun loadListings(category: com.mavrix.Olx_Rental.data.model.ListingCategory? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getListings().collect { allListings ->
                _listings.value = if (category != null) {
                    allListings.filter { it.category == category }
                } else {
                    allListings
                }
                _isLoading.value = false
            }
        }
    }

    fun getUserListings(userId: String): StateFlow<List<ListingModel>> {
        val userListings = MutableStateFlow<List<ListingModel>>(emptyList())
        viewModelScope.launch {
            repository.getUserListings(userId).collect { listings ->
                userListings.value = listings
            }
        }
        return userListings.asStateFlow()
    }

    fun createListing(listing: ListingModel, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            repository.createListing(listing)
                .onSuccess { onSuccess() }
                .onFailure { onError(it.message ?: "Failed to create listing") }
        }
    }

    fun deleteListingWithCallback(listingId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteListing(listingId)
                .onSuccess { onSuccess() }
        }
    }

    fun loadAllListings() {
        loadListings()
    }
    
    fun loadAllListingsForAdmin() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllListingsForAdmin().collect { allListings ->
                _listings.value = allListings
                _isLoading.value = false
            }
        }
    }
    
    fun refreshListings(category: com.mavrix.Olx_Rental.data.model.ListingCategory? = null) {
        loadListings(category)
    }
    
    // ========== Admin Functions ==========
    
    fun toggleListingStatus(listingId: String, isActive: Boolean) {
        viewModelScope.launch {
            try {
                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("listings")
                    .document(listingId)
                    .update("isActive", isActive)
                    .await()
                loadAllListingsForAdmin() // Refresh list with all listings
            } catch (e: Exception) {
                android.util.Log.e("ListingViewModel", "Error toggling listing status: ${e.message}")
            }
        }
    }
    
    fun deleteListing(listingId: String) {
        viewModelScope.launch {
            try {
                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("listings")
                    .document(listingId)
                    .delete()
                    .await()
                loadAllListingsForAdmin() // Refresh list with all listings
            } catch (e: Exception) {
                android.util.Log.e("ListingViewModel", "Error deleting listing: ${e.message}")
            }
        }
    }
}

