package com.cookandroid.heragit

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import java.text.SimpleDateFormat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    var textTitle = "Do github"
    var textContent = "오늘 커밋이 없어요"
    var channelId = "MYch"
    var channelName = "ch1"
    var notificationId:Int = 1002
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent != null){
            Log.e("알람", System.currentTimeMillis().toString())
            Toast.makeText(context,"alram~~",Toast.LENGTH_SHORT).show()
        }

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

}