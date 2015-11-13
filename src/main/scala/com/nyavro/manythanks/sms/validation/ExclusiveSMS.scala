package com.nyavro.manythanks.sms.validation

import android.content.{BroadcastReceiver, Context, Intent}
import android.preference.PreferenceManager
import android.telephony.SmsMessage
import android.util.Log
import org.scaloid.common.Preferences

/**
 * Listens incoming SMS, processes SMS matching by given predicate, aborts further broadcast of matching SMS
 */
class ExclusiveSMS(predicate: String => Boolean, onAccept: () => Unit) extends BroadcastReceiver {

  val Tag = "ExclusiveSMS"

  override def onReceive(context: Context, intent: Intent): Unit = {
    lazy val preferences = new Preferences(PreferenceManager.getDefaultSharedPreferences(context))
    Log.i(Tag, "Sms received")
    val message = parse(intent)
    if(predicate(message.getMessageBody)) {
      abortBroadcast()
      onAccept()
    }
    Log.i(Tag, "SMS message text: " + message.getDisplayMessageBody)
  }

  private def parse(intent: Intent) = {
    SmsMessage.createFromPdu(
      intent.getExtras.get("pdus").asInstanceOf[Array[Object]](0).asInstanceOf[Array[Byte]],
      intent.getStringExtra("format")
    )
  }
}
