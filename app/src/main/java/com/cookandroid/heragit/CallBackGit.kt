package com.cookandroid.heragit

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class CallBackGit:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gitcallback)
    }

    override fun onResume() {
        super.onResume()
        val CALLBACK_URL = "heragit://github-auth"

        val uri = intent.data
        Log.e("onResume",uri.toString())
        if (uri != null && uri.toString().startsWith(CALLBACK_URL)) {
            val access_token = uri.getQueryParameter("code")
            Log.e("onResume",access_token.toString())
        }
    }
}