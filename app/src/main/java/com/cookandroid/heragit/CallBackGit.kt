package com.cookandroid.heragit


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class CallBackGit:AppCompatActivity() {

    val url = URL("https://github.com/login/oauth/access_token")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gitcallback)
    }
    //todo: okHttp 사용법
    //https://soeun-87.tistory.com/23
    //oauth 참고 블로그
    //https://codeac.tistory.com/107
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

    private fun getAccessToken(code:String){
        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        val client = OkHttpClient()

        val request=Request.Builder()
            .header("client_id",BuildConfig.GITHUB_CLIENT_ID)
            .addHeader("client_secret",BuildConfig.GITHUB_CLIENT_SECRET)
            .addHeader("code",code)
            .url(url)
            .build()

        val response = client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                Thread{
                    var str = response.body?.string()
                    println(str)
                }.start()
            }
        })
    }


}