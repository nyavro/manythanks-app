package com.nyavro.manythanks.register

import android.content.{DialogInterface, Intent, Context, BroadcastReceiver}
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

  onCreate {
    paymentNotification.setText(R.string.retistration_payment)
    enterPhone.setText(R.string.enter_phone)
    enterPhone.addTextChangedListener(
      new TextWatcher {
        override def beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int): Unit = {}
        override def afterTextChanged(s: Editable): Unit = {}
        override def onTextChanged(s: CharSequence, start: Int, before: Int, count: Int): Unit = {
          ok.enabled = PhoneNumberUtils.isWellFormedSmsAddress(number.getText.toString)
        }
      }
    )
    ok.setText(R.string.ok)
    ok.onClick {
      Alert(getString(R.string.phone_valid_alert_title), Some(getString(R.string.check_your_phone) + enterPhone.getText.toString)).run(
        () => {},
        () => {}
      )
    }

    setContentView(new SVerticalLayout += paymentNotification += enterPhone += number += ok)
  }

  private def sendSms(phone:String, message:String) =
    SmsManager.getDefault.sendTextMessage(phone, null, message, null, null)
}
