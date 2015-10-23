package com.nyavro.manythanks.register

import java.util.concurrent.TimeUnit
import java.util.{Date, TimerTask, Timer}

import android.content._
import android.os.Handler
import android.telephony.{PhoneNumberUtils, SmsManager}
import android.text.{Editable, TextWatcher}
import com.nyavro.components.Alert
import com.nyavro.manythanks.R
import org.scaloid.common._

class RequestPhoneActivity extends SActivity {
  val Tag = "RequestPhoneActivity"

  lazy val paymentNotification = new STextView
  lazy val enterPhone = new STextView
  lazy val number = new SEditText
  lazy val ok = new SButton
  lazy val preferences = new Preferences(defaultSharedPreferences)
  lazy val smsReceiver = new SmsRegistrationReciever
  val RegistrationTag = "Registration"

  onCreate {
    paymentNotification.setText(R.string.retistration_payment)
    enterPhone.setText(R.string.enter_phone)
    number.addTextChangedListener(
      new TextWatcher {
        override def beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int): Unit = {}
        override def afterTextChanged(s: Editable): Unit = {}
        override def onTextChanged(s: CharSequence, start: Int, before: Int, count: Int): Unit = {
          ok.enabled = PhoneNumberUtils.isWellFormedSmsAddress(number.getText.toString)
        }
      }
    )
    ok.enabled = false
    ok.setText(R.string.ok)
    ok.onClick {
      Alert(getString(R.string.phone_valid_alert_title),
        Some(s"${getString(R.string.check_your_phone)}\n${number.getText.toString}")).run(
        () => {
        },
        () => {}
      )
    }
    setContentView(new SVerticalLayout += paymentNotification += enterPhone += number += ok)
  }

  private def register(phone:String) = {
    preferences.phoneToRegister = phone
    val filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED")
    registerReceiver(smsReceiver, filter)
    sendSms(phone, s"$RegistrationTag:$phone:${new NarrowedSpace(phone.hashCode).value}")
    val handler = new Handler
    val smsReceiveStop = new Timer
    smsReceiveStop.schedule(
      new TimerTask {
        override def run(): Unit = unregisterReceiver(smsReceiver)
      },
      new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2))
    )
  }

  private def sendSms(phone:String, message:String) = {
//    SmsManager.getDefault.sendTextMessage(phone, null, message, null, null)
  }
}
