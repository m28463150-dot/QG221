package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {
    private const val CHANNEL_ID = "quay_guett_alerts"
    private const val CHANNEL_NAME = "Alertes Quay Guett 221"
    private const val CHANNEL_DESC = "Rappels de concerts, billetterie et sorties musicales"

    fun initNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showSystemNotification(context: Context, id: Int, title: String, message: String) {
        initNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.quay_guett_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        try {
            notificationManager.notify(id, builder.build())
        } catch (e: SecurityException) {
            // Android 13+ POST_NOTIFICATIONS permission might not be granted yet, fallback gracefully
        }
    }

    fun notifyConcertReminder(context: Context, event: com.example.data.model.EventEntity) {
        showSystemNotification(
            context = context,
            id = event.id.hashCode(),
            title = "⏰ Rappel Concert : ${event.title}",
            message = "Votre concert à ${event.venueName} a lieu bientôt (${event.dateTimeText}) ! Préparez vos billets QR code dans l'application."
        )
    }

    fun notifyNewRelease(context: Context, track: com.example.data.model.TrackEntity) {
        showSystemNotification(
            context = context,
            id = track.id.hashCode(),
            title = "🔥 Nouveau Titre Disponible !",
            message = "« ${track.title} » par ${track.artistName} est maintenant en écoute sur Quay Guett 221."
        )
    }
}
