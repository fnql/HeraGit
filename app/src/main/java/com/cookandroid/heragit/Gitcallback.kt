package com.cookandroid.heragit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.Uri


class Gitcallback : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gitcallback)
    }

    override fun onResume() {
        super.onResume()
        val CALLBACK_URL = getString(R.string.github_callback)
        val uri: Uri? = intent.data
        if (uri != null && uri.toString().startsWith(CALLBACK_URL)) {
            val access_token: String? = uri.getQueryParameter("access_token")
        }
    }

}