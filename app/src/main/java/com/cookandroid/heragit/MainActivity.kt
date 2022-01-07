package com.cookandroid.heragit

import android.R.attr
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import java.util.*


class MainActivity : AppCompatActivity() {

    val url = URL("https://api.github.com/users/fnql/events?per_page=1")


/*  BuildConfig.GITHUB_API_KEY
    https://code.tutsplus.com/ko/tutorials/android-from-scratch-using-rest-apis--cms-27117
    https://jbin0512.tistory.com/118*/

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        timer.setIs24HourView(true)
        val TAG:String = "MainActivity : "

        button.setOnClickListener {

        }

        api_btn.setOnClickListener {
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

                override fun onPostExecute(result: String?) {
                    onNetworkFinished(result.toString())
                }
            }

            asyncTask.execute()
            //Log.d("Tag", aaa.toString())
        }

        alarm_start.setOnClickListener {
            var alarmMgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            var alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
                PendingIntent.getBroadcast(this, 0, intent, 0)
            }
            val hour: Int = timer.getHour()
            val minute: Int = timer.getMinute()
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }
            alarmMgr?.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmIntent
            )



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

    }


//https://calvinjmkim.tistory.com/16



    private fun onNetworkFinished(result: String) {
        var gson = Gson()
        var testModel = gson.fromJson(result, Array<UserEvent>::class.java)
        val commitTime = testModel[0].created_at.substring(0 until 10)
        val commitUser = testModel[0].payload.commits[0].author.name
        Log.d("Test",commitUser)

        Toast.makeText(getApplicationContext(), commitUser+commitTime,Toast.LENGTH_SHORT).show()
    }
}



