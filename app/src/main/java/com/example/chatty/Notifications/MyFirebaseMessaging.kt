package com.example.chatty.Notifications

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.chatty.DashboardScreen.ChatLogActivity
import com.example.chatty.DashboardScreen.Dashboardactivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessaging: FirebaseMessagingService() {
    @SuppressLint("obsoleteSdkInt")
    override fun onMessageReceived(mremoteMessage: RemoteMessage) {
    super.onMessageReceived(mremoteMessage)
    val sented = mremoteMessage.data["sented"]
    val user = mremoteMessage.data["user"]
    val sharedPref = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
    val currentOnlineUser = sharedPref.getString("currentUser","none")
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    if(firebaseUser!=null && sented==firebaseUser.uid)
    {
        if(currentOnlineUser!=user)
        {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                sendOreoNotification(mremoteMessage)
            }
            else
            {
                sendNotification(mremoteMessage)
            }
        }
    }
}
@RequiresApi(api = Build.VERSION_CODES.O)
private fun sendOreoNotification(mremoteMessage: RemoteMessage) {
    val user = mremoteMessage.data["user"]
    val icon = mremoteMessage.data["icon"]
    val title = mremoteMessage.data["title"]
    val body = mremoteMessage.data["body"]

    val notification = mremoteMessage.notification
    val j = user!!.replace("[\\D]".toRegex(),"").toInt()
    val intent = Intent (this , Dashboardactivity::class.java)

    val bundle = Bundle()
    bundle.putString("userid",user)
    intent.putExtras(bundle)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

    val pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT)

    val defauultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    val oreoNotification =
        OreoNotification(this)

    val builder: Notification.Builder = oreoNotification.getOreoNotification(title,body,pendingIntent,defauultSound,icon)
    var i=0
    if(j>0)
    {
        i=j
    }
    oreoNotification.getManager!!.notify(i,builder.build())
}

private fun sendNotification(mremoteMessage: RemoteMessage){
    val user = mremoteMessage.data["user"]
    val icon = mremoteMessage.data["icon"]
    val title = mremoteMessage.data["title"]
    val body = mremoteMessage.data["body"]
    val notification = mremoteMessage.notification
    val j = user!!.replace("[\\D]".toRegex(),"").toInt()
    val intent = Intent (this , ChatLogActivity::class.java)
    val bundle = Bundle()
    bundle.putString("userid",user)
    intent.putExtras(bundle)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    val pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT)
    val defauultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    val builder : NotificationCompat.Builder= NotificationCompat.Builder(this)
        .setSmallIcon(icon!!.toInt())
        .setContentTitle(title)
        .setContentText(body)
        .setAutoCancel(true)
        .setSound(defauultSound)
        .setContentIntent(pendingIntent)
    val noti = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    var i=0
    if(j>0)
    {
        i=j
    }
    noti.notify(i,builder.build())
}
}