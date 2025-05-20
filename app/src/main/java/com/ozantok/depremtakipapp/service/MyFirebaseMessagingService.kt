package com.ozantok.depremtakipapp.service

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ozantok.depremtakipapp.MainActivity
import com.ozantok.depremtakipapp.R
import com.ozantok.depremtakipapp.util.NotificationUtil

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", token)
        // Token'ı sunucuya göndermek istersen burada kullanabilirsin
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val lat = remoteMessage.data["quake_lat"]?.toDoubleOrNull()
        val lon = remoteMessage.data["quake_lon"]?.toDoubleOrNull()
        val magnitude = remoteMessage.data["magnitude"]
        val location = remoteMessage.data["location"]

        if (lat != null && lon != null && magnitude != null && location != null) {
            val intent = Intent(applicationContext, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("openMap", true)
                putExtra("quake_lat", lat)
                putExtra("quake_lon", lon)
            }

            val pendingIntent = PendingIntent.getActivity(
                applicationContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(applicationContext, "earthquake_channel_id")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("⚠️ Deprem Uyarısı")
                .setContentText("📍 $location - $magnitude büyüklüğünde deprem")
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(
                        "$location'da $magnitude büyüklüğünde bir deprem oldu.\nHarita üzerinden detayları görebilirsiniz."
                    )
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            if (
                NotificationManagerCompat.from(applicationContext).areNotificationsEnabled() &&
                ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationManagerCompat.from(applicationContext).notify(
                    System.currentTimeMillis().toInt(),
                    builder.build()
                )
            }
        } else {
            // Geriye uyumlu basic fallback
            remoteMessage.notification?.let {
                NotificationUtil.showBasicNotification(
                    context = applicationContext,
                    title = it.title ?: "Deprem Uyarısı",
                    message = it.body ?: "Yakınınızda bir deprem oldu!"
                )
            }
        }
    }

}
