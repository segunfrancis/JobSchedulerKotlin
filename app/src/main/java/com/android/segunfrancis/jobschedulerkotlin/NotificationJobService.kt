package com.android.segunfrancis.jobschedulerkotlin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationCompat

lateinit var mNotifyManager: NotificationManager
private const val PRIMARY_CHANNEL_ID: String = "primary_notification_channel"

class NotificationJobService : JobService() {
    override fun onStartJob(jobParameters: JobParameters): Boolean {

        // Create the notification channel
        createNotificationChannel()

        // Set up the notification content intent to launch the app when clicked
        val contentPendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Create Notification
        val builder = NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
            .setContentTitle("Job Service")
            .setContentText("Your Job ran successfully")
            .setContentIntent(contentPendingIntent)
            .setSmallIcon(R.drawable.ic_job_running)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)

        mNotifyManager.notify(0, builder.build())
        return false
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        /* The job will be rescheduled if it fails */
        return true
    }

    private fun createNotificationChannel() {
        // Define notification Channel object
        mNotifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Notification channels are only available in OREO and higher
        // So, add a check on SDK version
        if (SDK_INT >= O) {
            // Create Notification Channel with all the parameters
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Job Service Notification",
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Notifications from Job Service"

            mNotifyManager.createNotificationChannel(notificationChannel)
        }
    }
}