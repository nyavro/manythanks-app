package com.nyavro.manythanks.sms.validation

import android.content.{BroadcastReceiver, ContextWrapper, IntentFilter}
import android.util.Log

class BroadcastLifecycle(ctx:ContextWrapper) {
  private var receiverOp:Option[BroadcastReceiver] = None
  private val Tag: String = "SIntentService"

  def startListenSMS(receiver: BroadcastReceiver, filter: IntentFilter) = {
    receiverOp = Some(receiver)
    Log.i(Tag, "Registering receiver")
    ctx.registerReceiver(receiver, filter)
  }

  def stopListenSMS() = {
    Log.i(Tag, "Unregistering receiver")
    receiverOp.map(ctx.unregisterReceiver)
  }
}
