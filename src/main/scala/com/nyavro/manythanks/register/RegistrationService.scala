package com.nyavro.manythanks.register

import android.app.IntentService
import android.content.Intent
import android.preference.PreferenceManager
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import org.scaloid.common._
import rx.lang.scala.{Observable, Observer, Subscription}

/**
 * Performs chain of registration actions
 */
class RegistrationService extends IntentService(RegistrationService.Tag) {

  val GcmSenderId = "640727490581"

  lazy val preferences = new Preferences(PreferenceManager.getDefaultSharedPreferences(this))

  override def onHandleIntent(intent: Intent): Unit = {
    Log.i(RegistrationService.Tag, "Starting registration chain")
    Option(intent.getStringExtra(RegistrationService.Phone))
      .orElse(Some(preferences.phone(""))) match {
      case Some("") => sendResult(Some("No phone for registration provided"))
      case Some(phone) => register(phone)
    }
  }

  private def register(phone:String) = {
    (
      for (
        gcmToken <- {
          Log.i(RegistrationService.Tag, "GCM registration step")
          asObservable(new GcmRegistration(GcmSenderId, this).register)
        };
        userToken <- {
          Log.i(RegistrationService.Tag, "Backend registration step")
          asObservable(new BackendRegistration(gcmToken, phone).register)
        }
      ) yield userToken
    )
      .subscribe(
        userToken => {
          Log.i(RegistrationService.Tag, "User token registering")
          preferences.token = userToken.token
          preferences.userId = userToken.userId
          sendResult()
        },
        throwable => sendResult(Some(throwable.getMessage)),
        () => {}
      )
  }

  private def sendResult(error:Option[String] = None) = {
    Log.i(RegistrationService.Tag, "Registration complete broadcasting")
    val intent = new Intent(RegistrationService.Complete)
    error.map(
      message => {
        Log.e(RegistrationService.Tag, "Something went wrong during registration: " + message)
        intent.putExtra(RegistrationResult.ErrorMessage, message)
      }
    )
    intent.putExtra(RegistrationResult.Succeeded, !error.isDefined)
    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
  }

  private def asObservable[T](code:() => T):Observable[T] =
    Observable.create {
      (observer: Observer[T]) => {
        observer.onNext(code())
        Subscription()
      }
    }
}

object RegistrationService {
  val Tag = "RegistrationService"
  val Complete = "RegistrationComplete"
  val Phone = "Phone"
}