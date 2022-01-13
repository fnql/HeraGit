package com.cookandroid.heragit

import android.R.attr
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import android.util.Log
import java.io.BufferedReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import android.os.AsyncTask
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class MainActivity : AppCompatActivity() {

    val url = URL("https://api.github.com/users/fnql/events?per_page=1")


/*  BuildConfig.GITHUB_API_KEY*/
//TODO: 매일 알림 울리기
    //커밋 있을 시 알람 활성화 22/1/12
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
            val asyncTask = object : AsyncTask<Void, Int, String>() {
                override fun doInBackground(vararg p0: Void?): String? {
                    var result: String? = null
                    try {
                        // Open the connection

                        val conn = url.openConnection() as HttpURLConnection
                        conn.addRequestProperty("Authorization", "token ${BuildConfig.GITHUB_API_KEY}")
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
                        Log.e("REST_API", "GET method failed: " + e.message)
                        e.printStackTrace()
                    }
                    return result
                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onPostExecute(result: String?) {
                    onNetworkFinished(result.toString())
                }
            }

            asyncTask.execute()
            //Log.d("Tag", aaa.toString())
        }

        //alarm_start.setOnClickListener {}
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
        Toast.makeText(this,"다음 알람은 " + date_text+"입니다.",Toast.LENGTH_SHORT).show()
        val editor = getSharedPreferences("daily", MODE_PRIVATE).edit()
        editor.putLong("nextDate",calendar.timeInMillis)

        diaryAlarm(calendar)
    }

    private fun diaryAlarm(calendar: Calendar) {
        val diaryAl:Boolean = true
        val pm = this.packageManager
        var alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, 0)
        }
//        val receiver = ComponentName(this,DeviceBootReceiver::class.java)
        if (diaryAl){
            alarmMgr?.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmIntent
            )

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                alarmMgr?.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    alarmIntent
                )
            }
/*            pm.setComponentEnabledSetting(receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP)*/
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onNetworkFinished(result: String) {
        var gson = Gson()
        var testModel = gson.fromJson(result, Array<UserEvent>::class.java)
        val commitTime = testModel[0].created_at.substring(0 until 10)
        val commitUser = testModel[0].payload.commits[0].author.name
        Log.d("Test",commitUser)
        val today = LocalDate.now()
        if (commitTime.equals(today) && commitUser.equals("fnql") ){
            Toast.makeText(getApplicationContext(), "오늘 커밋 완료!",Toast.LENGTH_SHORT).show()
            alarmSetting()
        } else{

        }
        Toast.makeText(getApplicationContext(), commitUser+commitTime,Toast.LENGTH_SHORT).show()
    }
}



