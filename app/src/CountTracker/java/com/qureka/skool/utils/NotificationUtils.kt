package com.qureka.skool.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.qureka.skool.R
import com.qureka.skool.ServerConfig.chanalId
import com.qureka.skool.activity.SplashActivity

private const val CHANNEL_ID = "App Alert Notification ID"
private const val CHANNEL_NAME = "App Alert Notification"

class NotificationUtils(base: Context) : ContextWrapper(base) {

    private var notification: Notification? = null
    private var manager: NotificationManager? = null
    var intent: Intent? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels()
        }
    }

    fun getNotification(): Notification? = notification

    // Create channel for Android version 26+
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannels() {
        val channel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        channel.enableVibration(true)
        channel.setSound(null, null)
        getManager().createNotificationChannel(channel)
    }

    // Get Manager
    fun getManager(): NotificationManager {
        if (manager == null) manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return manager as NotificationManager
    }

    fun createNotification() {
        Log.e("***akd", "kjkj")
        intent = Intent(this, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        sendMyNotification()
    }

    private fun sendMyNotification() {
        val mNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent, FLAG_IMMUTABLE)
        } else {
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        }
        val notificationBuilder: Notification.Builder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationChannel.setSound(null, null)
                notificationChannel.enableVibration(true)
                mNotificationManager.createNotificationChannel(notificationChannel)
                Notification.Builder(applicationContext, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.applogo_noti)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Touch to close")
                    .setAutoCancel(true)
                    .setColor(Color.YELLOW)
                    .setContentIntent(pendingIntent)
            } else {
                Notification.Builder(applicationContext)
                    .setSmallIcon(R.mipmap.applogo_noti)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Touch to close")
                    .setAutoCancel(true)
                    .setColor(Color.YELLOW)
                    .setContentIntent(pendingIntent)
            }
        mNotificationManager.cancelAll()
//        notificationBuilder.style = Notification.BigTextStyle().bigText("Search My Phone is Active")
        notification = notificationBuilder.build()
        mNotificationManager.notify(chanalId, notification)
    }
}