package com.nyavro.manythanks

import android.content.{BroadcastReceiver, Context, Intent}
import com.nyavro.manythanks.contacts.ContactsView
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
  lazy val token = new STextView

  onCreate {
    message.setText("{empty}")
    send.setText("Send")
    send.onClick {
    }
    token.setText(preferences.gcmToken("not found"))
    info(preferences.gcmToken("not found"))
    sendSms.setText("Full sms registration")
    sendSms.onClick {
//      if(!preferences.isRegistered(false)) {
//        startActivity(new Intent(MainView.this, classOf[RegistrationActivity]))
//      }
    }

    register.setText("List")
    register.onClick {
      val intent = new Intent()
      intent.start[ContactsView]
    }
    number.setText(preferences.phone(""))
    setContentView(new SVerticalLayout += message += send += number += sendSms += register += token)
  }
}
