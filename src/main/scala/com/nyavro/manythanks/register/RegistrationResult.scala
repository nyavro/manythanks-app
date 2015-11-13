package com.nyavro.manythanks.register

import android.content.{Intent, Context, BroadcastReceiver}

class RegistrationResult(onComplete: ()=>Unit, onFailed: String => Unit) extends BroadcastReceiver {
  override def onReceive(context: Context, intent: Intent): Unit =
    if(intent.getBooleanExtra(RegistrationResult.Succeeded, false))
      onComplete()
    else
      onFailed(intent.getStringExtra(RegistrationResult.ErrorMessage))
}

object RegistrationResult extends Enumeration {
  val Succeeded = "succeeded"
  val ErrorMessage = "errorMessage"
}

