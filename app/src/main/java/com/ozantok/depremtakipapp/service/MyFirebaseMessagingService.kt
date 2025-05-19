package com.ozantok.depremtakipapp.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ozantok.depremtakipapp.util.NotificationUtil

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TOKEN", token)
        // Token'ı sunucuya göndermek istersen burada kullanabilirsin
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let {
            Log.d("FCM_MESSAGE", "Title: ${it.title}, Body: ${it.body}")

            // ✅ Burada bildirimi ekrana gösteriyoruz
            NotificationUtil.showEarthquakeNotification(
                applicationContext,
                it.body ?: "Yakınınızda bir deprem oldu!"
            )
        }
    }
}
