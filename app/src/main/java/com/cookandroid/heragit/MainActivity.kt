package com.cookandroid.heragit

import android.R.attr
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
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

import android.widget.AdapterView

import org.json.JSONException

import org.json.JSONObject

import org.json.JSONArray

import android.R.attr.key
import android.nfc.Tag
import android.view.View
import android.widget.AdapterView.OnItemClickListener

import android.widget.ArrayAdapter
import android.widget.Button
import com.google.gson.Gson
import java.io.IOException
import java.net.MalformedURLException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    var textTitle = "Do github"
    var textContent = "오늘 커밋이 없어요"
    var CHANNEL_ID = "MYch"
    var CHANNEL_NAME = "ch1"
    var notificationId:Int = 1002
    val url = URL("https://api.github.com/users/fnql/events?per_page=1")

/*  BuildConfig.GITHUB_API_KEY
    https://code.tutsplus.com/ko/tutorials/android-from-scratch-using-rest-apis--cms-27117
    https://jbin0512.tistory.com/118*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val TAG:String = "MainActivity : "

        button.setOnClickListener {
            var builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            createNotificationChannel(builder,notificationId)
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

    }
//https://calvinjmkim.tistory.com/16
    //콜 함수 데이터 메인에서 쓰는법

    private fun createNotificationChannel(builder:NotificationCompat.Builder,notificationId:Int) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val descriptionText = "1번 채널"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = descriptionText
            }
            channel.lightColor = Color.BLUE
            channel.enableVibration(true)
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            notificationManager.notify(notificationId,builder.build())
        }else{
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(notificationId,builder.build())
        }
    }

    private fun onNetworkFinished(result: String) {
        var gson = Gson()
        var testModel = gson.fromJson(result, Array<UserEvent>::class.java)
        Log.d("Test",testModel[0].actor.id)
    }
}



