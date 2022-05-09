package com.cookandroid.heragit

import android.app.Application

class MyApplication : Application() {
    private var sInstance: MyApplication? = null
    private fun get():MyApplication{
        return sInstance!!
    }

    companion object {
        lateinit var prefs : PreferenceUtils
    }

    override fun onCreate() {
        sInstance=this
        prefs = PreferenceUtils(applicationContext)
        super.onCreate()
    }
}