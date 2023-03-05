package com.cookandroid.heragit

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsIntent
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.drawable.ColorDrawable
import com.cookandroid.heragit.Model.UserEvent
import com.cookandroid.heragit.Service.CommitService
import com.cookandroid.heragit.thread.MyService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


//TODO: git while(당일) 당일 커밋여부 확인 - 다른 이름으로 커밋할 수 있
//Todo: 클래스별 버전정보 확인
class MainActivity : AppCompatActivity() {
    var customProgressDialog: ProgressDialog? = null
    var gitURL = "https://api.github.com/"

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        timer.setIs24HourView(true)
        val millis= MyApplication.prefs.dayTime

        val nextDate: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 22)
            set(Calendar.MINUTE, 0)
        }

        if (millis != -1L) {
            if (millis != null) {
                nextDate.setTimeInMillis(millis)
            }
        } else{
            Log.e("Main millis", "is Null")
        }

        val currentDateTime : Date =nextDate.getTime()
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

        //로딩창 객체 생성
        customProgressDialog = ProgressDialog(this)
        //로딩창을 투명하게
        customProgressDialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        github_check.setOnClickListener {
            //            customProgressDialog!!.show()
            val retrofit = Retrofit.Builder().baseUrl(gitURL).addConverterFactory(GsonConverterFactory.create()).build()
            val service = retrofit.create(CommitService::class.java)

            //todo:: userName 변수로 저장해두기
            service.getEvent("fnql")?.enqueue(object : Callback<List<UserEvent>> {
                override fun onResponse(call: Call<List<UserEvent>>, response: Response<List<UserEvent>>) {
                    if (response.isSuccessful) {
                        var result : List<UserEvent>? = response.body()
                        Log.d("LOG ","github Check 성공 : " + result.toString());
                    } else {
                        Log.d("LOG ","github Check 실패XXX");
                    }
                }

                override fun onFailure(call: Call<List<UserEvent>>, t: Throwable) {
                    Log.d("LOG ","github Check 통신 실패");
                }
            })
        }
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

        MyApplication.prefs.dayTime=calendar.timeInMillis
        diaryAlarm(calendar)
    }

    // 23/01/01 알람 동작 연습
    private fun diaryAlarm(calendar: Calendar) {
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 22)
        }
        val diaryAl:Boolean = true
        val pm = this.packageManager
        //val receiver = ComponentName(this,DeviceBootReceiver::class.java)
        var alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent,  0)
        }
        //하루단위 알림 작동X -> 15분 단위 알림 작동O
        if (diaryAl){
            alarmMgr.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmIntent
            )

/*            pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP)*/
        }
    }

    private fun login(context: Context){
        val loginUrl=Uri.Builder().scheme("https").authority("github.com")
            .appendPath("login")
            .appendPath("oauth")
            .appendPath("authorize")
            .appendQueryParameter("client_id",BuildConfig.GITHUB_CLIENT_ID)
            .appendQueryParameter("scope","repo")
            .build()

        CustomTabsIntent.Builder().build().also {
            it.launchUrl(context,loginUrl)
        }
    }

}