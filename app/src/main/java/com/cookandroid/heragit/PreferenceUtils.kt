package com.cookandroid.heragit

import android.content.Context
import android.content.SharedPreferences


class PreferenceUtils(context: Context) {
    private val prefsFilename = "prefs"
    private val prefsGitToken = "userGitToken"
    private val prefsGitUrl = "userGitUrl"
    private val prefsKeyDaily = "hours"
    private val prefsKeyDailyMin = "minute"
    private val prefs: SharedPreferences = context.getSharedPreferences(prefsFilename, Context.MODE_PRIVATE)

    var userGitToken: String?
        get() = prefs.getString(prefsGitToken, "")
        set(value) = prefs.edit().putString(prefsGitToken, value).apply()

    var userGitUrl:String?
        get() = prefs.getString(prefsGitUrl, "")
        set(value) = prefs.edit().putString(prefsGitUrl, value).apply()

    var dayTime: Long?
        get() = prefs.getLong(prefsKeyDaily,-1 )
        set(value) = prefs.edit().putLong(prefsKeyDaily, value!!).apply()

    var minTime: Long?
        get() = prefs.getLong(prefsKeyDailyMin,-1 )
        set(value) = prefs.edit().putLong(prefsKeyDailyMin, value!!).apply()
}