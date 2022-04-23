package com.cookandroid.heragit


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.cookandroid.heragit.Model.OauthLogin
import com.cookandroid.heragit.Model.OauthUser
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class CallBackGit:AppCompatActivity() {
    //git adb 무선 디버깅
    //https://iteastory.com/190
    val url = URL("https://github.com/login/oauth/access_token")
    var token = ""
    lateinit var pref: SharedPreferences
    lateinit var editor : SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gitcallback)

        //shared preference 초기화
        pref = getPreferences(Context.MODE_PRIVATE)
        editor = pref.edit()
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
//https://digitalbourgeois.tistory.com/59
    //ToDo: Thread 작동 X 끝나고 콜백 함수 호출하기
    private fun getAccessToken(code:String){
    try{

        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        val client = OkHttpClient()

        val json = JSONObject()
        json.put("client_id",BuildConfig.GITHUB_CLIENT_ID)
        json.put("client_secret",BuildConfig.GITHUB_CLIENT_SECRET)
        json.put("code",code)

        val body = json.toString().toRequestBody(JSON)
        val request=Request.Builder()
            .header("Accept","application/json")
            .url(url)
            .post(body)
            .build()
        val response = client.newCall(request).execute()

        var str :String?= response.body?.string()
        val res = Gson().fromJson<OauthLogin>(str,OauthLogin::class.java)
        PreferenceEdit.token=res.access_token
        Log.e("PreferenceEdit.token : ",PreferenceEdit.token)

        getUserName()
    } catch (e:Exception){
        System.err.println(e.toString())
    }
    }
    private fun getUserName(){
        Log.e("getUserName",": Start")
        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        val client = OkHttpClient()
        val nameUrl = URL("https://api.github.com/user")
        val request= Request.Builder()
            .header("Authorization","token ${PreferenceEdit.token}")
            .url(nameUrl)
            .get()
            .build()
        object : Thread() {
            override fun run() {
                val response = client.newCall(request).execute()

                var str = response.body?.string()
                val res = Gson().fromJson<OauthUser>(str, OauthUser::class.java)

                PreferenceEdit.url = res.url+"/events"
            }
        }.start()


    }
}