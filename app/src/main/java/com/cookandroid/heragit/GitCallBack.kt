package com.cookandroid.heragit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.R
import android.net.Uri


class GitCallBack: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page)
    }

    fun onResume() {
        super.onResume()

        // The following line will return "appSchema://appName.com".
        val CALLBACK_URL = resources.getString(R.string.insta_callback)
        val uri: Uri? = intent.data
        if (uri != null && uri.toString().startsWith(CALLBACK_URL)) {
            val access_token: String = uri.getQueryParameter("access_token")
        }
        // Perform other operations here.
    }
}