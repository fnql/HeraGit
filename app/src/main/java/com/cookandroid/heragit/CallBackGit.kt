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

    override fun onResume() {
        super.onResume()
        val CALLBACK_URL = "heragit://github-auth"

        val uri = intent.data
        Log.e("onResume",uri.toString())
        if (uri != null && uri.toString().startsWith(CALLBACK_URL)) {
            val access_token = uri.getQueryParameter("code")
            Log.e("onResume",access_token.toString())
            gitApiCheck(this)
        }
    }

    private fun gitApiCheck(context: Context?){
        val asyncTask = object : AsyncTask<Void, Int, String>() {
            override fun doInBackground(vararg p0: Void?): String? {
                var result: String? = null
                try {
                    // Open the connection
                    val conn = url.openConnection() as HttpURLConnection
                    conn.addRequestProperty("code", "token ${BuildConfig.GITHUB_API_KEY}")
                    conn.addRequestProperty("client_id", "token ${BuildConfig.GITHUB_CLIENT_ID}")
                    conn.addRequestProperty("client_secret", "token ${BuildConfig.GITHUB_CLIENT_SECRET}")
                    conn.requestMethod = "GET"
                    val ism = conn.inputStream
                    // Get the stream
                    val builder = StringBuilder()
                    val reader = BufferedReader(InputStreamReader(ism, "UTF-8"))
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        builder.append(line)
                    }

                    // Set the result
                    result = builder.toString()

                } catch (e: Exception) {
                    // Error calling the rest api
                    Log.e("GIT_ACCESS_TOKEN", e.message.toString())
                    e.printStackTrace()
                }
                return result
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onPostExecute(result: String?) {

            }
        }

        asyncTask.execute()
    }
}