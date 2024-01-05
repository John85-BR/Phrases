package org.hyperskill.phrases

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.app.NotificationCompat

class MyReceiver() : BroadcastReceiver() {


    private lateinit var mNotificationManager : NotificationManager
    private lateinit var appDatabase: AppDatabase

    companion object{
        const val CHANNEL_ID = "org.hyperskill.phrases"
        const val ID = 393939
    }

    override fun onReceive(context: Context, intent: Intent) {

        appDatabase = (context.applicationContext as ApplicationPhrase).database

        mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(appDatabase.getPhraseDao().getAll().isNotEmpty()){
            // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
            val i = Intent(context, MainActivity::class.java)
            val pIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT)
            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Your phrase of the day")
                .setContentText(appDatabase.getPhraseDao().getAll().last().phrase?:"Test")
                .setStyle(NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pIntent)
            mNotificationManager.notify(ID, notificationBuilder.build())
        }else{
            mNotificationManager.cancelAll()
        }
    }

}