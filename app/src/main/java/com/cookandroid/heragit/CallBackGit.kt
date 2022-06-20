package com.cookandroid.heragit


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.heragit.Model.OauthLogin
import com.cookandroid.heragit.Model.OauthUser
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.URL

class CallBackGit : AppCompatActivity() {

    val url = URL("https://github.com/login/oauth/access_token")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gitcallback)

        val CALLBACK_URL = "heragit://github-auth"
    }

    override fun onResume() {
        super.onResume()
        val CALLBACK_URL = "heragit://github-auth"
        val uri = intent.data
        if (uri != null && uri.toString().startsWith(CALLBACK_URL)) {
            val access_token = uri.getQueryParameter("code")
            getAccessToken(access_token.toString())
        } else {
            Log.e("CallBackGit","returnUri Error")
        }
    }


    private fun getAccessToken(code: String) {
        try {
            val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
            val client = OkHttpClient()

            val json = JSONObject()
            json.put("client_id", BuildConfig.GITHUB_CLIENT_ID)
            json.put("client_secret", BuildConfig.GITHUB_CLIENT_SECRET)
            json.put("code", code)

            val body = json.toString().toRequestBody(JSON)
            val request = Request.Builder()
                .header("Accept", "application/json")
                .url(url)
                .post(body)
                .build()
            object : Thread() {
                override fun run() {
                    val response = client.newCall(request).execute()

                    var accessTokenStr: String? = response.body?.string()
                    val res = Gson().fromJson<OauthLogin>(accessTokenStr, OauthLogin::class.java)
                    MyApplication.prefs.userGitToken = res.access_token

                }
            }.start()
            getUserName()
        } catch (e: Exception) {
            System.err.println(e.toString())
        }
    }

    private fun getUserName() {
        try {
            val client = OkHttpClient()
            val nameUrl = URL("https://api.github.com/user")
            Log.e("getUerName","token ${MyApplication.prefs.userGitToken}")
            val request = Request.Builder()
                .addHeader("Authorization", "token ${MyApplication.prefs.userGitToken}")
                .url(nameUrl)
                .build()
            var userNameStr=""
            object : Thread() {
                override fun run() {
                    val response = client.newCall(request).execute()
                    userNameStr = response.body?.string().toString()
                    if (userNameStr != null) {
                        Log.e("Thread",userNameStr)
                    }

                }
            }.start()
            val res = Gson().fromJson<OauthUser>(userNameStr, OauthUser::class.java)
            MyApplication.prefs.userGitUrl = res.url + "/events"
        } catch (e: Exception) {
            System.err.println(e.toString())
        }
    }
}