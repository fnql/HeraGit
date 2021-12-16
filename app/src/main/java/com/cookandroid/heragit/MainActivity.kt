package com.cookandroid.heragit

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_main.*
import android.os.AsyncTask
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import android.util.JsonReader





class MainActivity : AppCompatActivity() {
    var textTitle = "Do github"
    var textContent = "오늘 커밋이 없어요"
    var CHANNEL_ID = "MYch"
    var CHANNEL_NAME = "ch1"
    var notificationId:Int = 1002
    var githubEndpoint: URL = URL("https://api.github.com/")
    var myConnection: HttpsURLConnection = githubEndpoint.openConnection() as HttpsURLConnection


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        button.setOnClickListener {
            var builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            createNotificationChannel(builder,notificationId)
        }

        AsyncTask.execute {
            // All your networking logic
            // should be here
            myConnection.setRequestProperty("User-Agent", "my-rest-app-v0.1");
            myConnection.setRequestProperty("Accept",
                "application/vnd.github.v3+json");
            myConnection.setRequestProperty("Contact-Me",
                "dbwhddnr00@naver.com");
            if (myConnection.getResponseCode() == 200) {
                val responseBody: InputStream = myConnection.inputStream
                var responseBodyReader: InputStreamReader = InputStreamReader(responseBody, "UTF-8")
                val jsonReader = JsonReader(responseBodyReader)
                jsonReader.beginObject() // Start processing the JSON object

                while (jsonReader.hasNext()) { // Loop through all keys
                    val key = jsonReader.nextName() // Fetch the next key
                    if (key == "organization_url") { // Check if desired key
                        // Fetch the value as a String
                        val value = jsonReader.nextString()

                        // Do something with the value
                        // ...
                        break // Break out of the loop
                    } else {
                        jsonReader.skipValue() // Skip values of other keys
                    }
                }
                jsonReader.close();
                myConnection.disconnect();
            } else {
                // Error handling code goes here
            }
        }

    }
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