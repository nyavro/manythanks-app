package com.nyavro.manythanks.register

import android.content.Context
import android.util.Log
import com.google.android.gms.gcm.{GcmPubSub, GoogleCloudMessaging}
import com.google.android.gms.iid.InstanceID

/**
 * Performs GCM Registration
 */
class GcmRegistration(senderId:String, ctx:Context) extends Registration[String] {

  private val Topics = Array("global")

  private val Tag = "GcmRegistration"
  /**
   * Requests token at GCM
   * @return
   */
  def register():String = {
    Log.i(Tag, "GCM Registering")
    val gcmToken = InstanceID
      .getInstance(ctx)
      .getToken(
        senderId,
        GoogleCloudMessaging.INSTANCE_ID_SCOPE,
        null
      )
    Log.i(Tag, s"GCM token: $gcmToken")
    subscribeTopics(gcmToken)
    gcmToken
  }

  /**
   * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
   */
  private def subscribeTopics(token: String) = {
    Log.i(Tag, "Subscribing topics")
    val pubSub = GcmPubSub.getInstance(ctx)
    Topics.foreach {
      topic => pubSub.subscribe(token, "/topics/" + topic, null)
    }
  }
}
