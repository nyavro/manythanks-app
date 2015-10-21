package com.nyavro.manythanks

import android.content.{Intent, Context, BroadcastReceiver}
import android.net.Uri
import android.telephony.SmsManager
import android.util.Log
import com.google.android.gms.common.{ConnectionResult, GoogleApiAvailability}
import com.nyavro.manythanks.telephony.GsmInfo
import org.scaloid.common._

class MainView extends SActivity {

  val PlayServicesResolutionRequest = 9000
  val Tag = "MainActivity"

  lazy val message = new STextView
  lazy val preferences = new Preferences(defaultSharedPreferences)
  lazy val receiver = new BroadcastReceiver {
    override def onReceive(context: Context, intent: Intent): Unit = {
      if(preferences.sentTokenToServer(false)) {
        message.setText("pref sent ok")
      } else
        message.setText("pref sent failed")
    }
  }
  lazy val send = new SButton
  lazy val sendSms = new SButton

  lazy val number = new SEditText

  onCreate {
    message.setText("{empty}")
    send.setText("Send")
    send.onClick {
      if (isPlayServicesAvailable) {
        message.setText("sent request")
        startService(new Intent(this, classOf[RegistrationIntentService]))
      }
    }

    sendSms.setText("Send sms")
    sendSms.onClick {
      sendSms(number.getText.toString, "testtest")
    }

    setContentView(new SVerticalLayout += message += send += number += sendSms)
  }

  private def sendSms(phone:String, message:String) =
    SmsManager.getDefault.sendTextMessage(phone, null, message, null, null)

  def isPlayServicesAvailable = {
    val availability = GoogleApiAvailability.getInstance()
    val result = availability.isGooglePlayServicesAvailable(this)
    if(result!=ConnectionResult.SUCCESS) {
      if(availability.isUserResolvableError(result)) {
        availability.getErrorDialog(this, result, PlayServicesResolutionRequest).show()
      } else {
        Log.i(Tag, "This device is not supported")
      }
      false
    }
    else {
      true
    }
  }
}
