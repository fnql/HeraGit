package com.cookandroid.heragit


import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class CallBackGit : AppCompatActivity() {
    var customProgressDialog: ProgressDialog? = null
    val url = URL("https://github.com/login/oauth/access_token")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //로딩창 객체 생성
        customProgressDialog = ProgressDialog(this)
        //로딩창을 투명하게
        customProgressDialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        customProgressDialog!!.show()

        val CALLBACK_URL = "heragit://github-auth"

    }

    //resume 실행 안함
    override fun onResume() {
        Log.e("onResume","Start")
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
                    var response = client.newCall(request).execute()
                    if (response.isSuccessful){
                        var accessTokenStr: String? = response.body?.string()
                        val res = Gson().fromJson<OauthLogin>(accessTokenStr, OauthLogin::class.java)
                        MyApplication.prefs.userGitToken = res.access_token
                        getUserName()
                    }
                    else{
                        val responseCode = response.toString()
                        var responseData = response.body?.string()
                        Log.d("---","---");
                        Log.e("//===========//","================================================");
                        Log.d("getAccessToken","\nOK HTTP 동기 GET 요청 실패]");
                        Log.d("", "\n[에러 코드 : $responseCode]");
                        Log.d("", "\n[에러 값 : $responseData]");
                        Log.e("//===========//","================================================");
                        Log.d("---","---");
                    }
                }
            }.start()

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
                    if (response.isSuccessful) {
                        userNameStr = response.body?.string().toString()
                        if (userNameStr != null) {
                            val res = Gson().fromJson<OauthUser>(userNameStr, OauthUser::class.java)
                            //todo:: user name만 뽑아내기
                            MyApplication.prefs.userGitUrl = res.url + "/events"
                        }
                    } else{
                        val responseCode = response.toString()
                        var responseData = response.body?.string()
                        Log.e("//===========//","================================================");
                        Log.d("getAccessToken","\nOK HTTP 동기 GET 요청 실패]");
                        Log.d("", "\n[에러 코드 : $responseCode]");
                        Log.d("", "\n[에러 값 : $responseData]");
                        Log.e("//===========//","================================================");
                    }
                }
            }.start()

        } catch (e: Exception) {
            System.err.println(e.toString())
        }
    }
}