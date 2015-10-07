package com.nyavro.manythanks

import android.app.{NotificationManager, PendingIntent}
import android.content.{Context, Intent}
import android.media.RingtoneManager
import android.os.Bundle
import android.util.Log
import android.support.v4.app.NotificationCompat
import com.google.android.gms.gcm.GcmListenerService

/**
 * Created by eny on 06.10.15.
 */
class MyGcmListenerService extends GcmListenerService {

  val Tag = "MTGcmListenerService"
  /**
   * Called when message is received.
   *
   * @param from SenderID of the sender.
   * @param data Data bundle containing message data as key/value pairs.
   *             For Set of keys use data.keySet().
   */
  override def onMessageReceived(from: String, data: Bundle) = {
    val message = data.getString("message")
    Log.d(Tag, "From: " + from)
    Log.d(Tag, "Message: " + message)
    if (from.startsWith("/topics/")) {
      Log.i(Tag, "topic")
    }
    else {
      Log.i(Tag, "nottopic")
    }
    sendNotification(message)
  }

  def sendNotification(message: String) = {
    val intent = new Intent(this, classOf[Nothing])
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
    getSystemService(Context.NOTIFICATION_SERVICE).asInstanceOf[NotificationManager]
      .notify(
        0,
        new NotificationCompat.Builder(this)
          .setSmallIcon(R.drawable.ic_stat_ic_notification)
          .setContentTitle("GCM Message")
          .setContentText(message)
          .setAutoCancel(true)
          .setSound(defaultSoundUri)
          .setContentIntent(pendingIntent).build
      )
  }
}
