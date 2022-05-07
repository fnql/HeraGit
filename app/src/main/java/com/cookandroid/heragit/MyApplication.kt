package com.cookandroid.heragit

import android.app.Application

class MyApplication : Application() {
    companion object {
        lateinit var prefs : PreferenceUtils
    }

    override fun onCreate() {
        prefs = PreferenceUtils(applicationContext)
        super.onCreate()
    }
}