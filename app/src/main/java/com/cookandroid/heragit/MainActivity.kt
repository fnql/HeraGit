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
import android.view.View
import android.widget.AdapterView.OnItemClickListener

import android.widget.ArrayAdapter
import android.widget.Button
import java.io.IOException
import java.net.MalformedURLException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    var textTitle = "Do github"
    var textContent = "오늘 커밋이 없어요"
    var CHANNEL_ID = "MYch"
    var CHANNEL_NAME = "ch1"
    var notificationId:Int = 1002
    val url = URL("https://api.github.com/users/fnql/events")

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
            }

        }


        btnData.setOnClickListener {
                object : Thread() {
                    override fun run() {
                        items.clear()
                        val date = Date()
                        date.setTime(date.getTime() - 1000 * 60 * 60 * 24) // 현재의 날짜에서 1일을 뺀 날짜
                        val sdf = SimpleDateFormat("yyyyMMdd")
                        val dateStr: String = sdf.format(date) // 20210316
                        val urlAddress: String =
                            address.toString() + "?key=" + key + "&targetDt=" + dateStr
                        try {
                            val url = URL(urlAddress)
                            val `is` = url.openStream()
                            val isr = InputStreamReader(`is`)
                            val reader = BufferedReader(isr)
                            val buffer = StringBuffer()
                            var line = reader.readLine()
                            while (line != null) {
                                buffer.append(
                                    """
                                $line
                                
                                """.trimIndent()
                                )
                                line = reader.readLine()
                            }
                            val jsonData = buffer.toString()

                            // jsonData를 먼저 JSONObject 형태로 바꾼다.
                            val obj = JSONObject(jsonData)
                            // obj의 "boxOfficeResult"의 JSONObject를 추출
                            val boxOfficeResult = obj["boxOfficeResult"] as JSONObject
                            // boxOfficeResult의 JSONObject에서 "dailyBoxOfficeList"의 JSONArray 추출
                            val dailyBoxOfficeList =
                                boxOfficeResult["dailyBoxOfficeList"] as JSONArray
                            for (i in 0 until dailyBoxOfficeList.length()) {
                                val temp = dailyBoxOfficeList.getJSONObject(i)
                                val movieNm = temp.getString("movieNm")
                                items.add(movieNm)
                            }
                            runOnUiThread { adapter.notifyDataSetChanged() }
                        } catch (e: MalformedURLException) {
                            e.printStackTrace()
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }.start()
            }
        // 리스트뷰의 아이템 클릭 이벤트 > 토스트 메시지 띄우기
        // 리스트뷰의 아이템 클릭 이벤트 > 토스트 메시지 띄우기
        listView.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            val data = parent.getItemAtPosition(position) as String
            Toast.makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()
        })

    }
//https://calvinjmkim.tistory.com/16

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
}



