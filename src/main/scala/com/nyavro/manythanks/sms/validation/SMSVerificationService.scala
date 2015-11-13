package com.nyavro.manythanks.sms.validation

import java.util.concurrent.TimeUnit

import android.app.IntentService
import android.content.{Intent, IntentFilter}
import android.preference.PreferenceManager
import android.telephony.SmsManager
import android.util.Log
import com.nyavro.manythanks.register.RegistrationService
import org.scaloid.common.Preferences

class SMSVerificationService extends IntentService(SMSVerificationService.Tag) {

  lazy val preferences = new Preferences(PreferenceManager.getDefaultSharedPreferences(this))
  lazy val broadcastLifecycle = new BroadcastLifecycle(this)

  override def onHandleIntent(intent: Intent): Unit = {
    Option(intent.getStringExtra(SMSVerificationService.Phone)).map {
      phone => {
        val message = new RegistrationMessage(phone, System.currentTimeMillis())
        broadcastLifecycle.startListenSMS(
          new ExclusiveSMS(
            received => message.fit(received),
            () => {
              Log.i(SMSVerificationService.Tag, "Starting registration chain")
              val intent = new Intent(this, classOf[RegistrationService])
              intent.putExtra(RegistrationService.Phone, phone)
              startService(intent)
            }
          ),
          new IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        )
        sendSms(phone, message.value)
        Thread.sleep(TimeUnit.MINUTES.toMillis(1))
      }
    }
  }

  override def onDestroy() = {
    broadcastLifecycle.stopListenSMS()
  }

  /**
   * Sends SMS
   * @param phone target phone number
   * @param message message to send
   */
  private def sendSms(phone:String, message:String) = {
    Log.i(SMSVerificationService.Tag, s"Sending $message to $phone")
    SmsManager.getDefault.sendTextMessage(phone, null, message, null, null)
  }
}

object SMSVerificationService {
  private val Tag = "SMSVerificationService"
  val Phone = "phone"
}