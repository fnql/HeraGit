package com.cookandroid.heragit.thread

import android.os.Handler
import java.lang.Exception

class ServiceThread(handler: Handler) : Thread() {
    var handler: Handler
    var isRun = true
    fun stopForever() {
        synchronized(this) { isRun = false }
    }

    override fun run() {
        //반복적으로 수행할 작업을 한다.
        while (isRun) {
            handler.sendEmptyMessage(0) //쓰레드에 있는 핸들러에게 메세지를 보냄
            try {
                sleep(1000) //10초씩 쉰다.
            } catch (e: Exception) {
            }
        }
    }

    init {
        this.handler = handler
    }
}