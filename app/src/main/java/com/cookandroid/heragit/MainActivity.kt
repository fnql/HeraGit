package com.cookandroid.heragit

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

/*  BuildConfig.GITHUB_API_KEY*/
//TODO: git while(당일) 당일 커밋여부 확인 - 다른 이름으로 커밋할 수 있
    //안드로이도 HeraGit://github-auth

    val url = URL("https://github.com/login/oauth/access_token")
    var token = ""
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        timer.setIs24HourView(true)
        val TAG:String = "MainActivity : "

        val sharedPreferences = getSharedPreferences("daily", MODE_PRIVATE)
        val millis = sharedPreferences.getLong("nextDate",Calendar.getInstance().timeInMillis)

        val nextDate: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 22)
            set(Calendar.MINUTE, 0)
        }
        nextDate.setTimeInMillis(millis)
        val currentDateTime=nextDate.getTime()
        val date_text = SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분",Locale.getDefault()).format(currentDateTime)
        Toast.makeText(this,"다음 알람은 " + date_text+"입니다.",Toast.LENGTH_SHORT).show()

        if (Build.VERSION.SDK_INT>=23){
            timer.setHour(22)
            timer.setMinute(0)
        } else{
            timer.setCurrentHour(22)
            timer.setCurrentMinute(0)
        }

        alarm_start.setOnClickListener {
            alarmSetting()
        }

        alarm_cancel.setOnClickListener {
            var alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            var intent = Intent(this, AlarmReceiver::class.java)
            var pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0)
            alarmMgr.cancel(pendingIntent)
            Toast.makeText(
                this,
                "Alarm 취소",
                Toast.LENGTH_SHORT
            ).show()
        }
        github_apps.setOnClickListener {
            login(this)

        }
    }

    override fun onResume() {
        super.onResume()
        val CALLBACK_URL = "heragit://github-auth"

        val uri = intent.data
        Log.e("onResume",uri.toString())
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
        val request= Request.Builder()
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
                        token=str.toString().substring(13,tokenLast-1)
                        //token 값 저장하는법
                        Log.e("response",token)
                    }
                    else{
                        Log.e("response","str value error")
                    }
                }.start()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun alarmSetting(){
        val hour: Int = timer.getHour()
        val minute: Int = timer.getMinute()
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }
        if (calendar.before(Calendar.getInstance())){
            calendar.add(Calendar.DATE,1)
        }
        val currentDateTime=calendar.getTime()
        val date_text = SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분",Locale.getDefault()).format(currentDateTime)
        Toast.makeText(this,"***다음 알람은 " + date_text+"입니다.",Toast.LENGTH_SHORT).show()
        val editor = getSharedPreferences("daily", MODE_PRIVATE).edit()
        editor.putLong("nextDate",calendar.timeInMillis)

        diaryAlarm(calendar)
    }

    private fun diaryAlarm(calendar: Calendar) {
        val diaryAl:Boolean = true
        val pm = this.packageManager
        val receiver = ComponentName(this,DeviceBootReceiver::class.java)
        var alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent,  0)
        }
        val aint = Intent(this,AlarmReceiver::class.java)
        Log.e("diaryAlarm", token)
        aint.putExtra("accessToken",token)
        if (diaryAl){
            alarmMgr?.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmIntent
            )

            pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP)
        }
    }
    private fun login(context: Context){
        val loginUrl=Uri.Builder().scheme("https").authority("github.com")
            .appendPath("login")
            .appendPath("oauth")
            .appendPath("authorize")
            .appendQueryParameter("client_id",BuildConfig.GITHUB_CLIENT_ID)
            .appendQueryParameter("scope","repo:status")
            .build()

        CustomTabsIntent.Builder().build().also {
            it.launchUrl(context,loginUrl)
        }
    }

}