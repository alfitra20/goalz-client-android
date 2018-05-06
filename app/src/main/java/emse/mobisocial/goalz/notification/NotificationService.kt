package emse.mobisocial.goalz.notification

import android.app.Notification.DEFAULT_ALL
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.ui.GoalActivity

/**
 * Created by MobiSocial EMSE Team on 4/30/2018.
 */

const val RECOMMENDATION_NOTIFICATION_ID = 1
const val RECOMMENDATION_NOTIFICATION_CHANNEL_ID = "CH1"
const val RECOMMENDATION_NOTIFICATION_CHANNEL_NAME = "RecommendationChannel"
const val RECOMMENDATION_NOTIFICATION_CHANNEL_DESCRIPTION = "Channel for recommendation messages"

class NotificationService : FirebaseMessagingService() {

    override fun onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(applicationContext)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null) {
            Log.d("NOTIF SERVICE", "Message Notification Body: " + remoteMessage.notification!!.body!!)
        }
        if (remoteMessage.data != null){

            Log.d("NOTIF SERVICE", "Message Data: " + remoteMessage.data["goalTitle"])
            Log.d("NOTIF SERVICE", "Message Data: " + remoteMessage.data["recommendation"])
            Log.d("NOTIF SERVICE", "Message Data: " + remoteMessage.data["goal"])

            val title = remoteMessage.data["goalTitle"]
            val recommendation = remoteMessage.data["recommendation"]
            val goal = remoteMessage.data["goal"]

            sendNotification(title!!, goal!!, recommendation!!)
        }
    }

    private fun sendNotification(goalTitle : String, goalId : String, recommendationId : String) {
        val notificationManager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(application, GoalActivity::class.java)
        intent.putExtra("goal_id", goalId)
        val pIntent = PendingIntent.getActivity(application, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val description = "Goal '$goalTitle'"
        val notificationBuilder = NotificationCompat.Builder(applicationContext, RECOMMENDATION_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(application.getString(R.string.notification_new_recommendation_title))
                .setContentText(description)
                .setSmallIcon(R.drawable.ic_mode_comment_white_24dp)
                .setContentIntent(pIntent)
                .setAutoCancel(true)

        notificationBuilder.setDefaults(DEFAULT_ALL)

        notificationManager.notify(RECOMMENDATION_NOTIFICATION_ID, notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context : Context) {
        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val id = RECOMMENDATION_NOTIFICATION_CHANNEL_ID;
        val name = RECOMMENDATION_NOTIFICATION_CHANNEL_NAME
        val description = RECOMMENDATION_NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(id, name, importance)
        mChannel.description = description
        mChannel.enableVibration(true)
        mChannel.vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
        mChannel.lightColor = Color.RED
        mChannel.enableLights(true)
        mNotificationManager.createNotificationChannel(mChannel)
    }
}