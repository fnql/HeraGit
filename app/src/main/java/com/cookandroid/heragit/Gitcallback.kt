package com.cookandroid.heragit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class GitCallBack : AppCompatActivity() {

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.e("onNewIntent",intent?.data.toString())
        intent?.data?.getQueryParameter("code")?.let {
            val gitcode= it
            Log.e("onNewIntent",gitcode)
            // 엑세스 토큰 받아와야함
            launch(GlobalScope.coroutineContext) {
                viewmodel.getAccessToken(it)
                Toast.makeText(this@MainActivity, "로그인 되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}