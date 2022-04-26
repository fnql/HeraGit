package com.cookandroid.heragit

import android.content.Context
import android.content.SharedPreferences
import android.widget.DatePicker
import java.util.*

class PreferenceUtils(context: Context) {
    private val prefsFilename = "prefs"
    private val prefsKeyEdt = "myEditText"
    private val prefsKeyDaily =
    private val prefs: SharedPreferences = context.getSharedPreferences(prefsFilename, 0)

    var myEditText: String?
        get() = prefs.getString(prefsKeyEdt, "")
        set(value) = prefs.edit().putString(prefsKeyEdt, value).apply()

    var dailyTime: Date?
        get() = prefs.getDate(prefsKeyDaily, )
        set(value) = prefs.edit().putString(prefsKeyEdt, value).apply()
}