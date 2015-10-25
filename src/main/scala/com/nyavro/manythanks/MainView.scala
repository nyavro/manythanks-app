package com.nyavro.manythanks

import android.content.{BroadcastReceiver, Context, Intent}
import com.nyavro.manythanks.register.{Registration, RequestPhoneActivity}
import org.scaloid.common._

class MainView extends SActivity {

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
  lazy val register = new SButton

  onCreate {
    message.setText("{empty}")
    send.setText("Send")
    send.onClick {
    }

    sendSms.setText("Full sms registration")
    sendSms.onClick {
      if(!preferences.isRegistered(false)) {
        startActivity(new Intent(MainView.this, classOf[RequestPhoneActivity]))
      }
    }

    register.setText("Register")
    register.onClick {
      new Registration(preferences).register("gcm_dummy_token", preferences.phone(""))
    }
    number.setText(preferences.phone(""))
    setContentView(new SVerticalLayout += message += send += number += sendSms += register)
  }

}
