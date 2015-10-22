package com.nyavro.manythanks

import android.content.{BroadcastReceiver, Context, Intent}
import android.telephony.SmsManager
import android.util.Log
import com.eny.smallpoll.view.QuestionView
import com.nyavro.manythanks.register.{RequestPhoneActivity, Registration}
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

    sendSms.setText("Send sms")
    sendSms.onClick {
      val intent = new Intent(MainView.this, classOf[RequestPhoneActivity])
      startActivity(intent)
    }

    register.setText("Register")
    register.onClick {
      new Registration(preferences).check()
    }

    setContentView(new SVerticalLayout += message += send += number += sendSms += register)
  }

}
