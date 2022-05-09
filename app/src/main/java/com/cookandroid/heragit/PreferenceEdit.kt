package com.cookandroid.heragit

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

object PreferenceEdit{
    private val PREF_TOKEN = "token"
    private val PREF_NAME = "horang"
    private val PREF_URL = "URL"
    private val PREF_MODE = Context.MODE_PRIVATE
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PREF_MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor)->Unit) {
        val editor = edit()
        operation(edit())
        edit().apply()
    }

    var token: String
        get() = sharedPreferences.getString(PREF_TOKEN, "").toString()
        set(value) = sharedPreferences.edit {
            it.putString(PREF_TOKEN, value).apply()
        }
    var url: String
        get() = sharedPreferences.getString(PREF_URL, "").toString()
        set(value) = sharedPreferences.edit {
            it.putString(PREF_URL, value).apply()
        }
}
