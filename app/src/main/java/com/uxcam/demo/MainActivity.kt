package com.uxcam.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the Analytics SDK
        val analytics = UXCamSDK.Builder().apiKey("").build().start(context = this);
        // Start a new session
        findViewById<Button>(R.id.start_analytics).setOnClickListener {
            analytics.startSession()
            analytics.addEvent("Started Session", mapOf("session_start_time" to LocalDateTime.now()))
        }

        // End the session
        findViewById<Button>(R.id.end_analytics).setOnClickListener {
            analytics.addEvent("Ended Session", mapOf("session_end_time" to LocalDateTime.now()))
            analytics.endSession()
            // Debug: Print session data
            val sessionData = analytics.getSessionData()
            println("Session Data: $sessionData")
        }

    }
}
