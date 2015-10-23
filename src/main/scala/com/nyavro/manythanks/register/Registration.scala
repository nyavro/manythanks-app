package com.nyavro.manythanks.register

import java.util.concurrent.TimeUnit

import android.content.{Context, Intent}
import com.google.android.gms.common.{ConnectionResult, GoogleApiAvailability}
import org.scaloid.common.Preferences

class Registration(preferences:Preferences)(implicit ctx:Context) {

  val GCMToken = "GCM_Token"
  val Tag = "Registration"
  val PlayServicesResolutionRequest = 9000
  val GoogleServiceConnectionFailure = "GoogleServiceConnectionFailure"
  val GoogleServiceConnectionUnresolvableFailure = "GoogleServiceConnectionUnresolvableFailure"
  val RegistrationTag = "Registration"

  def check() = {
  }

//  def check() =
//    if(preferences.isRegistered(false)) {
//      //Ok
//    }
//    else {
//      val phone = requestUserPhone()
//      val phoneHash = hash(phone)
//      val message = s"$RegistrationTag:$phone:$hash"
//      sendSms(message)
//      recieveSms()
//      unregisterAfter(TimeUnit.MINUTES.toMillis(5))
//    }
//
//  def onSms(message:String) = {
//    val confirmation = s"$RegistrationTag:(.+):(\\d+)"r
//    message match {
//      case confirmation(phone, code) => proceedRegister(phone, code.toLong)
//    }
//  }
//
//  def proceedRegister(phone:String, code:Long) = {
//    if (hash(phone).equals(code)) {
//      getGcmRegistration(phone)
//    }
//  }

  def getGcmRegistration(phone:String) = {
    if(!isPlayServicesAvailable.isDefined) {
      val intent = new Intent(ctx, classOf[RegistrationIntentService])
      intent.putExtra(Registration.PhoneExtra, phone)
      ctx.startService(intent)
    }
  }

  def isPlayServicesAvailable:Option[String] = {
    val availability = GoogleApiAvailability.getInstance()
    val result = availability.isGooglePlayServicesAvailable(ctx)
    if (result != ConnectionResult.SUCCESS)
      Some(
        if (availability.isUserResolvableError(result))
          GoogleServiceConnectionFailure
        else
          GoogleServiceConnectionUnresolvableFailure
      )
    else
      None
  }
}

object Registration {
  val PhoneExtra = "Phone"
}
