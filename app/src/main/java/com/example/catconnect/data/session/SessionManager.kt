package com.example.catconnect.data.session

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("catconnect_session", Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED_IN, false)
    fun currentName(): String? = prefs.getString(KEY_NAME, null)
    fun currentEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun login(name: String, email: String) {
        prefs.edit()
            .putBoolean(KEY_LOGGED_IN, true)
            .putString(KEY_NAME, name)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val KEY_LOGGED_IN = "logged_in"
        const val KEY_NAME = "name"
        const val KEY_EMAIL = "email"
    }
}
