package com.nyavro.manythanks.register

import java.util.concurrent.TimeUnit

import android.app.AlertDialog
import android.content._
import android.telephony.{SmsManager, SmsMessage}
import android.text.{Editable, TextWatcher}
import android.util.Log
import com.nyavro.components.Alert
import com.nyavro.manythanks.R
import com.nyavro.manythanks.rx.RxThread._
import org.scaloid.common._
import rx.lang.scala.{Observable, Observer, Subscription}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class SMSRegistrationActivity extends SActivity with UnregisterReceiver {

  val Tag = "SMSRegistrationActivity"

  lazy val paymentNotification = new STextView
  lazy val enterPhone = new STextView
  lazy val number = new SEditText
  lazy val ok = new SButton
  lazy val preferences = new Preferences(defaultSharedPreferences)

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
    val filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED")
    val message = new RegistrationMessage(phone, System.currentTimeMillis())
    spinnerDialog(R.string.registering_sms_title, R.string.registering_sms_message).map (
      progress =>
        Observable.create {
          (observer: Observer[SmsMessage]) => {
            registerReceiver(new SmsRegistrationReceiver(observer), filter)
            Subscription()
          }
        }
          .timeout(Duration(2, TimeUnit.MINUTES))
          .execAsync
          .subscribe (
            sms => {
              if (message.fit(sms.getMessageBody)) {
                preferences.phone = phone
                Log.d(Tag, "OriginatingAddress: " + sms.getOriginatingAddress)
                Log.d(Tag, "Registered phone: " + phone)
                startService(new Intent(ctx, classOf[RegistrationIntentService]))
              } else {
                new AlertDialog.Builder(ctx)
                  .setTitle(R.string.error)
                  .setMessage(R.string.invalid_registration_message)
                  .show()
              }
            },
            throwable => {
              Log.e(Tag, "SMS Registration Error: " + throwable.getMessage)
              progress.dismiss()
              new AlertDialog.Builder(ctx)
                .setTitle(R.string.error)
                .setMessage(R.string.sms_registration_failed)
                .show()
            },
            () => {
              progress.dismiss()
            }
          )
    )
    sendSms(phone, message.value)
  }

  private def sendSms(phone:String, message:String) = {
    SmsManager.getDefault.sendTextMessage(phone, null, message, null, null)
  }
}

class SmsRegistrationReceiver(observer:Observer[SmsMessage]) extends BroadcastReceiver {

  val Tag = "RegisterationReceiver"
  
  override def onReceive(context: Context, intent: Intent): Unit = {
    val message = parse(intent)
    if(message.getMessageBody.startsWith(RegistrationMessage.prefix)) {
      abortBroadcast()
      observer.onNext(message)
      observer.onCompleted()
    }
    Log.d(Tag, "SMS message text: " + message.getDisplayMessageBody)
  }

  private def parse(intent: Intent) = SmsMessage.createFromPdu(
    intent.getExtras.get("pdus").asInstanceOf[Array[Object]](0).asInstanceOf[Array[Byte]],
    intent.getStringExtra("format")
  )
}
