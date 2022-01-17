package com.cookandroid.heragit

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class DeviceBootReceiver: BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            if(Objects.equals(intent.action, "android.intent.action.BOOT_COMPLETED")){
                val alarmIntent = Intent(context, AlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0)
                val manager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val sharedPreferences = context.getSharedPreferences("daily",
                    AppCompatActivity.MODE_PRIVATE
                )
                val millis = sharedPreferences.getLong("nextDate",Calendar.getInstance().timeInMillis)

                val currentCal = Calendar.getInstance()
                val nextDate: Calendar = GregorianCalendar()
                nextDate.setTimeInMillis(sharedPreferences.getLong("nextDate",millis))
                if (currentCal.after(nextDate)){
                    nextDate.add(Calendar.DATE,1)
                }
                val currentDateTime=nextDate.getTime()
                val date_text = SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분",Locale.getDefault()).format(currentDateTime)
                Toast.makeText(context.applicationContext,"[재부팅]다음 알람은 " + date_text+"입니다.", Toast.LENGTH_SHORT).show()

                if (manager!=null){
                    manager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        nextDate.timeInMillis,
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                    )
                }
            }

        }
    }
}
