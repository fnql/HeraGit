package com.cookandroid.heragit

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context,"alram~~",Toast.LENGTH_SHORT).show()
        Log.e("Alarm","알람입니다.")
    }
}