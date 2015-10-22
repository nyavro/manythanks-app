package com.nyavro.manythanks.register

import android.content.{Intent, Context, BroadcastReceiver}
import android.telephony.SmsMessage
import android.util.Log

class SmsRegistrationReciever extends BroadcastReceiver {
  override def onReceive(context: Context, intent: Intent): Unit = {
    val pdus=intent.getExtras.get("pdus").asInstanceOf[Array[Object]]
    val message=SmsMessage.createFromPdu(pdus(0).asInstanceOf[Array[Byte]])
    if(message.getMessageBody.startsWith("test")) {
      abortBroadcast
    }
    Log.d("SMSReceiver","SMS message sender: " + message.getOriginatingAddress)
    Log.d("SMSReceiver","SMS message text: " + message.getDisplayMessageBody)
  }
}
