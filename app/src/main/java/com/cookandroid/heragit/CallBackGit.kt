package com.cookandroid.heragit


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
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
        if (uri != null && uri.toString().startsWith(CALLBACK_URL)) {
            val access_token = uri.getQueryParameter("code")
            getAccessToken(access_token.toString())
        }
    }

    private fun getAccessToken(code:String){
        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        val client = OkHttpClient()

        val json = JSONObject()
        json.put("client_id",BuildConfig.GITHUB_CLIENT_ID)
        json.put("client_secret",BuildConfig.GITHUB_CLIENT_SECRET)
        json.put("code",code)

        val body = json.toString().toRequestBody(JSON)
        val request=Request.Builder()
            .url(url)
            .post(body)
            .build()

        val response = client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                Thread{
                    var str = response.body?.string()
                    Log.e("response str : ",str.toString())
                    if (str.toString() != null && str.toString().startsWith("access_token")) {
                        var tokenLast= str.toString().indexOf("scope")
                        var token=str.toString().substring(13,tokenLast-1)
                        Log.e("response",token)
                    }
                    else{
                        Log.e("response","str value error")
                    }
                }.start()
            }
        })
    }
}