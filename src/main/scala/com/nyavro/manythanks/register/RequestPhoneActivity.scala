package com.nyavro.manythanks.register

import java.util.concurrent.TimeUnit
import java.util.{Date, Timer, TimerTask}

import android.content._
import android.os.Handler
import android.preference.PreferenceManager
import android.telephony.{SmsManager, SmsMessage}
import android.text.{Editable, TextWatcher}
import android.util.Log
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
  lazy val smsReceiver = new SmsRegistrationReceiver

  onCreate {
    paymentNotification.setText(R.string.retistration_payment)
    enterPhone.setText(R.string.enter_phone)
    number.setText("+79513789255")
    number.addTextChangedListener(
      new TextWatcher {
        override def beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int): Unit = {}
        override def afterTextChanged(s: Editable): Unit = {}
        override def onTextChanged(s: CharSequence, start: Int, before: Int, count: Int): Unit = {
          ok.enabled = !s.toString.isEmpty
        }
      }
    )
    ok.enabled = !number.getText.toString.isEmpty
    ok.setText(R.string.ok)
    ok.onClick {
      Alert(getString(R.string.phone_valid_alert_title),
        Some(s"${getString(R.string.check_your_phone)}\n${number.getText.toString}")).run(
        register(number.getText.toString)
      )
    }
    setContentView(new SVerticalLayout += paymentNotification += enterPhone += number += ok)
  }

  private def register(phone:String) = {
    preferences.phoneToRegister = phone
    val registrationId = System.currentTimeMillis()
    preferences.registrationId = registrationId
    val filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED")
    registerReceiver(smsReceiver, filter)
    //TODO:Start blocking spinner
    sendSms(phone, new RegistrationMessage(phone, registrationId).value)
    val handler = new Handler
    val smsReceiveStop = new Timer
    smsReceiveStop.schedule(
      new TimerTask {
        override def run() =
          try {
            unregisterReceiver(smsReceiver)
          } catch {
            case e:Throwable => Log.e(Tag, e.getMessage)
          }
      },
      new Date(registrationId + TimeUnit.MINUTES.toMillis(2))
    )
  }

  private def sendSms(phone:String, message:String) = {
    SmsManager.getDefault.sendTextMessage(phone, null, message, null, null)
  }

  onPause {
    try {
      unregisterReceiver(smsReceiver)
    } catch {
      case e:Throwable => Log.e(Tag, e.getMessage)
    }
  }
}

class SmsRegistrationReceiver extends BroadcastReceiver {

  val Tag = "RegisterationReceiver"
  
  override def onReceive(context: Context, intent: Intent): Unit = {
    val preferences = new Preferences(PreferenceManager.getDefaultSharedPreferences(context))
    val phone = preferences.phoneToRegister("")
    val registrationId = preferences.registrationId(System.currentTimeMillis())
    val registrationMessage = new RegistrationMessage(phone, registrationId)
    val pdus=intent.getExtras.get("pdus").asInstanceOf[Array[Object]]
    val message=SmsMessage.createFromPdu(pdus(0).asInstanceOf[Array[Byte]])
    if(message.getMessageBody.startsWith(registrationMessage.prefix)) {
      abortBroadcast()
      if(registrationMessage.fit(message.getMessageBody) && !phone.isEmpty) {
        Log.d(Tag, "OriginatingAddress: " + message.getOriginatingAddress)
        Log.d(Tag, "Registered phone: " + phone)
        preferences.phone = phone
        //TODO:Stop blocking spinner
        context.startService(new Intent(context, classOf[RegistrationIntentService]))
      }
    }
    Log.d(Tag, "SMS message text: " + message.getDisplayMessageBody)
  }
}
