package com.cookandroid.heragit

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class CallBackGit:AppCompatActivity() {

    val url = URL("https://github.com/login/oauth/access_token")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gitcallback)
    }
//https://soeun-87.tistory.com/23
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