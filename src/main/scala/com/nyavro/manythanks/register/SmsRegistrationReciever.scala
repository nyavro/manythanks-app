package com.nyavro.manythanks.register

import android.content.{Intent, Context, BroadcastReceiver}
import android.telephony.SmsMessage
import android.util.Log

class SmsRegistrationReciever extends BroadcastReceiver {
  override def onReceive(context: Context, intent: Intent): Unit = {
    val pdus=intent.getExtras.get("pdus").asInstanceOf[Array[Object]]
    val shortMessage=SmsMessage.createFromPdu(pdus(0).asInstanceOf[Array[Byte]])

    Log.d("SMSReceiver","SMS message sender: " + shortMessage.getOriginatingAddress)
    Log.d("SMSReceiver","SMS message text: " + shortMessage.getDisplayMessageBody)
  }
}
