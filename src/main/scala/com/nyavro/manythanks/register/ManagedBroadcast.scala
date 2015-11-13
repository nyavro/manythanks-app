package com.nyavro.manythanks.register

import android.content.{IntentFilter, BroadcastReceiver, ContextWrapper}
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import org.scaloid.common.Destroyable

trait ManagedBroadcast extends ContextWrapper with Destroyable {
  /**
   * Internal implementation for (un)registering the receiver. You do not need to call this method.
   */
  def registerLocalReceiver(receiver: BroadcastReceiver, filter: IntentFilter): Unit = {
    onDestroy {
      Log.i("ScalaUtils", "Unregister BroadcastReceiver: " + receiver)
      try {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
      } catch {
        // Suppress "Receiver not registered" exception
        // Refer to http://stackoverflow.com/questions/2682043/how-to-check-if-receiver-is-registered-in-android
        case e: IllegalArgumentException => e.printStackTrace()
      }
    }
    LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)
  }
}