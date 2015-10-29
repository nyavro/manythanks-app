package com.nyavro.manythanks.register

import android.app.IntentService
import android.content.Intent
import android.preference.PreferenceManager
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.google.android.gms.gcm.{GcmPubSub, GoogleCloudMessaging}
import com.google.android.gms.iid.InstanceID
import com.nyavro.manythanks.R
import org.scaloid.common.Preferences

class RegistrationIntentService extends IntentService(RegistrationIntentService.Tag) {

  val preferences = new Preferences(PreferenceManager.getDefaultSharedPreferences(this))
  /**
   * Server API Key help
AIzaSyDVV5tut_3_WCfBR4HgoQmcH-zN2_QIlrE
Sender ID help
640727490581
   * @param intent
   */

  override def onHandleIntent(intent: Intent): Unit = {
    val phone = preferences.phone("")
    try {
      val instanceID = InstanceID.getInstance(this)
      val token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null)
      Log.i(RegistrationIntentService.Tag, "GCM Registration Token: " + token)
      sendRegistrationToServer(token, phone)
      subscribeTopics(token)
      preferences.gcmRegistered = true
    }
    catch {
      case e: Exception =>
        Log.d(RegistrationIntentService.Tag, "Failed to complete token refresh", e)
        preferences.gcmRegistered = false
    }
    val registrationComplete = new Intent(RegistrationIntentService.RegistrationComplete)
    LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete)
  }

  private def sendRegistrationToServer(gcmToken: String, phone:String) = {
    new BackendRegistration(preferences)(this).register(gcmToken, phone)
  }

  /**
   * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
   */
  private def subscribeTopics(token: String) = {
    val pubSub = GcmPubSub.getInstance(this)
    RegistrationIntentService.Topics.foreach {
      topic => pubSub.subscribe(token, "/topics/" + topic, null)
    }
  }
}

object RegistrationIntentService {
  val Tag = "RegIntentService"
  val Topics = Array("global")
  val SentTokenToServer = "sentTokenToServer"
  val RegistrationComplete = "registrationComplete"
}
