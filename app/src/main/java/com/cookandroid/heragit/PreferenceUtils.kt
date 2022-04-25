package com.cookandroid.heragit

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtils(context: Context) {
    private val prefsFilename = "prefs"
    private val prefsKeyEdt = "myEditText"
    private val prefsKeyDaily = "현재시간변수"
    private val prefs: SharedPreferences = context.getSharedPreferences(prefsFilename, 0)

    var myEditText: String?
        get() = prefs.getString(prefsKeyEdt, "")
        set(value) = prefs.edit().putString(prefsKeyEdt, value).apply()

    var dailyTime: String?
        get() = prefs.getString(prefsKeyEdt, "")
        set(value) = prefs.edit().putString(prefsKeyEdt, value).apply()
}