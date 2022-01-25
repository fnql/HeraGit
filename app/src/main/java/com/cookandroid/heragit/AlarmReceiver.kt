package com.cookandroid.heragit

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    var textTitle = "Do github"
    var textContent = "오늘 커밋이 없어요"
    var channelId = "MYch"
    var channelName = "ch1"
    var notificationId:Int = 1002
    val url = URL("https://api.github.com/users/fnql/events?per_page=1")
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent != null){
            Log.e("알람", System.currentTimeMillis().toString())
            Toast.makeText(context,"alram~~",Toast.LENGTH_SHORT).show()
        }
        gitApiCheck(context)

    }

    private fun alarmStart(context: Context?) {
        val notificationManager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
                or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingl = PendingIntent.getActivity(context,0,notificationIntent,0)

        var builder = NotificationCompat.Builder(context!!, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingl)
        Log.e("알람 작동 유무", Build.VERSION.SDK_INT.toString())
        gitApiCheck(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val descriptionText = "1번 채널"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = descriptionText
            }

            channel.lightColor = Color.BLUE
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)

        }
        if (notificationManager != null){
            notificationManager.notify(notificationId,builder.build())
            val nextTime = Calendar.getInstance()
            nextTime.add(Calendar.DATE,1)

            val editor = context.getSharedPreferences("daily", AppCompatActivity.MODE_PRIVATE).edit()
            editor.putLong("nextDate",nextTime.timeInMillis)
            editor.apply()

            val currentDateTime=nextTime.getTime()
            val date_text = SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분",Locale.getDefault()).format(currentDateTime)
            Toast.makeText(context,"다음 알람은 " + date_text+"입니다.",Toast.LENGTH_SHORT).show()

        }
    }

    private fun gitApiCheck(context: Context?){
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
                onNetworkFinished(result.toString(),context)
            }
        }

        asyncTask.execute()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onNetworkFinished(result: String,context: Context?){
        var gson = Gson()
        var testModel = gson.fromJson(result, Array<UserEvent>::class.java)
        val commitTime = testModel[0].created_at.substring(0 until 10)
        val commitHour = testModel[0].created_at.substring(12 until 13).toInt()
        val commitUser = testModel[0].payload.commits[0].author.name
        val today = LocalDate.now()

        // created_at시간이 현재 시간과 다름, 영국 시간으로 추측 한국-9시
        //전날 15:00 ~ 당일 15:00까지 당일 커밋
        if ((commitTime.equals(today.toString()) && commitHour<15) ||
            (commitHour>=15 && commitTime.equals(today.minusDays(1).toString()))){
            Toast.makeText(context, "오늘 커밋 완료!",Toast.LENGTH_SHORT).show()
            Log.e("AlarmTest", "OO $commitUser$commitTime OO")

        } else{
            Toast.makeText(context, "오늘 커밋 없음",Toast.LENGTH_SHORT).show()
            Log.e("AlarmTest", "XX $commitUser$commitTime XX")
            alarmStart(context)
        }

    }
}