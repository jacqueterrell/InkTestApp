package com.example.inktestapp.utils

import android.content.SharedPreferences

class AppSharedPrefs(private val sharedPreferences: SharedPreferences) {

    var hasEnabledSmoothPen: Boolean
        get() = sharedPreferences
            .getBoolean(SMOOTH_PEN, false)
        set(hasEnabled) = sharedPreferences
            .edit()
            .putBoolean(SMOOTH_PEN, hasEnabled)
            .apply()

    var hasEnabledDarkTheme: Boolean
        get() = sharedPreferences
            .getBoolean(DARK_THEME, false)
        set(hasEnabled) = sharedPreferences
            .edit()
            .putBoolean(DARK_THEME, hasEnabled)
            .apply()

    var penSensitivityValue: Float
        get() = sharedPreferences
            .getFloat(PEN_SENSITIVITY, 1.0f)
        set(value) = sharedPreferences
            .edit()
            .putFloat(PEN_SENSITIVITY, value)
            .apply()

    companion object {
        private const val SMOOTH_PEN = "smoothPen"
        private const val DARK_THEME = "darkTheme"
        private const val PEN_SENSITIVITY = "penSensitivity"

    }
}