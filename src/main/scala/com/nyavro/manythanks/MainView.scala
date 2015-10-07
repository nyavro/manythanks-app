package com.nyavro.manythanks

import android.content.{Intent, Context, BroadcastReceiver}
import android.util.Log
import com.google.android.gms.common.{ConnectionResult, GoogleApiAvailability}
import org.scaloid.common._

/**
 * Created by eny on 06.10.15.
 */
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

  onCreate {
    message.setText("{empty}")
    send.setText("Send")
    send.onClick {
      if (isPlayServicesAvailable) {
        message.setText("sent request")
        startService(new Intent(this, classOf[RegistrationIntentService]))
      }
    }
    setContentView(new SVerticalLayout += message += send)
  }

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
