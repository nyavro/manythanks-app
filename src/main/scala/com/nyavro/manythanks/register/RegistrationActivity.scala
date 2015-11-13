package com.nyavro.manythanks.register

import java.util.concurrent.TimeUnit

import android.app.AlertDialog
import android.content.{Intent, IntentFilter}
import android.text.{Editable, TextWatcher}
import com.nyavro.components.Alert
import com.nyavro.manythanks.R
import com.nyavro.manythanks.sms.validation.SMSVerificationService
import org.scaloid.common._

import scala.concurrent.ExecutionContext.Implicits.global
class RegistrationActivity extends SActivity with ManagedBroadcast {

  val Tag = "RegistrationActivity"

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
      Alert(
        getString(R.string.phone_valid_alert_title),
        Some(s"${getString(R.string.check_your_phone)}\n${number.getText.toString}")
      ).run(register(number.getText.toString))
    }
    setContentView(new SVerticalLayout += paymentNotification += enterPhone += number += ok)
  }

  private def register(phone:String) =
    spinnerDialog(R.string.registering_sms_title, R.string.registering_sms_message).map {
      progress => {
        info("Registration started")
        registerLocalReceiver(
          new RegistrationResult(
            () => {
              info("Registration finished")
              progress.dismiss()
              new AlertDialog.Builder(ctx)
                .setTitle(R.string.registering_sms_title)
                .setMessage(R.string.backend_registration_succeeded)
                .show()
            },
            err => {
              info(s"Registration failed $err")
              error(err)
              progress.dismiss()
              new AlertDialog.Builder(ctx)
                .setTitle(R.string.error)
                .setMessage(R.string.sms_registration_failed)
                .show()
            }
          ),
          new IntentFilter(RegistrationService.Complete)
        )
        info("Starting RegistrationService")
        val intent = new Intent(ctx, classOf[SMSVerificationService])
        intent.putExtra(SMSVerificationService.Phone, phone)
        startService(intent)
        Thread.sleep(TimeUnit.MINUTES.toMillis(1))
        info("Dismissing progress on timeout")
        progress.dismiss()
      }
    }
}
