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
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class MainActivity : AppCompatActivity() {

/*  BuildConfig.GITHUB_API_KEY*/
//TODO: git while(당일) 당일 커밋여부 확인 - 다른 이름으로 커밋할 수 있
//TODO: 깃캣코드 참고해서 링크 연결하기

//http://localhost:8080/auth/Heragit//ok
//이거 변경해서 사용 스프링 써야하나...
//https://github.com/login/oauth/authorize?scope=repo:status%20read:repo_hook%20user:email&client_id=
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
            //링크타고 콜랙url에서 뒤code값 get요청으로 받으면 될듯
            val intent=Intent(Intent.ACTION_VIEW,Uri.parse("https://github.com/login/oauth/authorize?scope=repo,user&client_id=2f977b3d7307952e0d33"))
            startActivity(intent)

        }

    }
    override fun onResume() {
        super.onResume()
        val CALLBACK_URL = getString(R.string.github_callback)
        //intent.data null 오류
        val uri: Uri? = intent.data
        Log.e("onResume",uri.toString())
        if (uri != null && uri.toString().startsWith(CALLBACK_URL)) {
            val access_token: String? = uri.getQueryParameter("access_token")

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
        }




