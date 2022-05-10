package com.cookandroid.heragit

import android.content.Context
import android.content.SharedPreferences


class PreferenceUtils(context: Context) {
    private val prefsFilename = "prefs"
    private val prefsKeyEdt = "myEditText"
    private val prefsKeyDaily = "hours"
    private val prefsKeyDailyMin = "minute"
    private val prefs: SharedPreferences = context.getSharedPreferences(prefsFilename, Context.MODE_PRIVATE)

    var uerGitToken: String?
        get() = prefs.getString(prefsKeyEdt, "")
        set(value) = prefs.edit().putString(prefsKeyEdt, value).apply()

    var dayTime: Long?
        get() = prefs.getLong(prefsKeyDaily,-1 )
        set(value) = prefs.edit().putLong(prefsKeyDaily, value!!).apply()

    var minTime: Long?
        get() = prefs.getLong(prefsKeyDailyMin,-1 )
        set(value) = prefs.edit().putLong(prefsKeyDailyMin, value!!).apply()
}