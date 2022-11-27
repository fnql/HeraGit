package com.cookandroid.heragit

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.cookandroid.heragit.Model.UserEvent
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
    var textTitleSuccess = "DID github"
    var textContentSuccess = "오늘 커밋 했어요!"
    var channelId = "MYch"
    var channelName = "ch1"
    var notificationId: Int = 1002
    var url = URL(MyApplication.prefs.userGitUrl)
    var count = 1L

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        //count 오류 확인
        count+=1
        MyApplication.prefs.testData = LocalDate.now().toString()
        if (intent != null) {
            Log.e("알람", System.currentTimeMillis().toString())
            Toast.makeText(context, "alarm~~", Toast.LENGTH_SHORT).show()
        } else {
            Log.e("alarmReceiverNull", System.currentTimeMillis().toString())
            Toast.makeText(context, "ReceiveNull~~", Toast.LENGTH_SHORT).show()
        }

        gitApiCheck(context)
    }

    private fun gitApiCheck(context: Context?) {


        val asyncTask = object : AsyncTask<Void, Int, String>() {
            override fun doInBackground(vararg p0: Void?): String? {

                var result: String? = null
                try {
                    // Open the connection
                    val conn = url.openConnection() as HttpURLConnection
                    conn.addRequestProperty("Authorization", "token ${MyApplication.prefs.userGitToken}")
                    conn.requestMethod = "GET"
                    val ism = conn.inputStream
                    // Get the stream
                    val builder = StringBuilder()
                    val reader = BufferedReader(InputStreamReader(ism, "UTF-8"))
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        builder.append(line)
                    }

                    result = builder.toString()

                } catch (e: Exception) {
                    Log.e("REST_API", "GET method failed: " + e.message)
                    e.printStackTrace()
                }
                return result
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onPostExecute(result: String?) {
                onNetworkFinished(result.toString(), context)
            }
        }

        asyncTask.execute()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onNetworkFinished(result: String, context: Context?) {
        var gson = Gson()
        var testModel = gson.fromJson(result, Array<UserEvent>::class.java)
        val commitTime = testModel[0].created_at.substring(0 until 10)
        val commitHour = testModel[0].created_at.substring(12 until 13).toInt()
        val commitUser = testModel[0].payload.commits[0].author.name
        val today = LocalDate.now()

        // created_at시간이 현재 시간과 다름, 영국 시간으로 추측 한국-9시
        //전날 15:00 ~ 당일 15:00까지 당일 커밋
        if ((commitTime == today.toString() && commitHour < 15) ||
            (commitHour >= 15 && commitTime == today.minusDays(1).toString())
        ) {
            Toast.makeText(context, "오늘 커밋 완료####"+MyApplication.prefs.testData, Toast.LENGTH_SHORT).show()
            Log.e("AlarmTest", "OO $commitUser$commitTime OO")
            alarmStartSuccess(context)

        } else {
            Toast.makeText(context, "오늘 커밋 없음@@@@"+MyApplication.prefs.testData, Toast.LENGTH_SHORT).show()
            Log.e("AlarmTest", "XX $commitUser$commitTime XX")
            alarmStart(context)
        }

    }

    //todo: 1120 알림 확인
    private fun alarmStart(context: Context?) {
        val notificationManager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
                or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        val pendingl = PendingIntent.getActivity(context, 0, notificationIntent, 0)

        var builder = NotificationCompat.Builder(context!!, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(textTitle)
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingl)

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
        if (notificationManager != null) {
            notificationManager.notify(notificationId, builder.build())
            val nextTime = Calendar.getInstance()
            nextTime.add(Calendar.DATE, 1)

            MyApplication.prefs.dayTime=nextTime.timeInMillis
            val currentDateTime = nextTime.getTime()
            val date_text =
                SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분", Locale.getDefault()).format(
                    MyApplication.prefs.dayTime
                )
            Toast.makeText(context, "다음 알람은 " + date_text + "입니다.", Toast.LENGTH_SHORT).show()

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun alarmStartSuccess(context: Context?) {
        val notificationManager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP
                or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingl = PendingIntent.getActivity(context, 0, notificationIntent, 0)

        var builder = NotificationCompat.Builder(context!!, channelId)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(textTitleSuccess)
            .setContentText(textContentSuccess)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingl)

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
        if (notificationManager != null) {
            notificationManager.notify(notificationId, builder.build())
            val nextTime = Calendar.getInstance()
            nextTime.add(Calendar.DATE, 1)

            MyApplication.prefs.dayTime=nextTime.timeInMillis
            val date_text =
                SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분", Locale.getDefault()).format(
                    MyApplication.prefs.dayTime
                )
            Toast.makeText(context, "다음 알람은 " + date_text + "입니다.", Toast.LENGTH_SHORT).show()

        }
    }
}