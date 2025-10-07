package com.mavrix.Olx_Rental.ui.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ThemeViewModel(private val context: Context) : ViewModel() {
    
    private var _isDarkMode by mutableStateOf(false)
    val isDarkMode: Boolean
        get() = _isDarkMode

    init {
        loadThemePreference()
    }

    private fun loadThemePreference() {
        viewModelScope.launch {
            val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
            _isDarkMode = prefs.getBoolean("is_dark_mode", false)
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            _isDarkMode = !_isDarkMode
            val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("is_dark_mode", _isDarkMode).apply()
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            _isDarkMode = enabled
            val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
            prefs.edit().putBoolean("is_dark_mode", enabled).apply()
        }
    }
}
