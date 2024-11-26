package com.openclassrooms.hexagonal.games.data.service


import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.openclassrooms.hexagonal.games.R

class FirebaseMessaging : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("NOTIF TOKEN", "Refreshed token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("NOTIF MSG", "Message received from : ${message.from}")

        //verification is not empty
        message.data.isNotEmpty().let {
            Log.d("NOTIF MSG", "Message data payload: ${message.data}")
        }

        //affichage message si notification
        message.notification?.let {
            val title = it.title
            val body = it.body
            Log.d("NOTIF MSG", "Message Notification Body: ${it.body}")
            sendNotification(title,body)
        }

    }

    //fun pour afficher une notification
    fun sendNotification(title: String?, body: String?){
        val channelId = "notif_channel"

        //création du chanal de notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "général Channel"
            val importance = android.app.NotificationManager.IMPORTANCE_DEFAULT
            val channel = android.app.NotificationChannel(channelId, name, importance)
            val notificationManager = getSystemService(android.app.NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }

        //construction de la notification
        val notificationBuilder = NotificationCompat.Builder(this,channelId)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title?:"Notification Hexagonal Games")
            .setContentText(body?:"Message reçu")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(1, notificationBuilder.build())
        }
    }
}