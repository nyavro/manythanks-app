package com.nyavro.manythanks.register

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import com.github.kevinsawicki.http.HttpRequest
import com.nyavro.manythanks.R
import com.nyavro.manythanks.rx.RxThread._
import org.scaloid.common.Preferences
import rx.lang.scala.{Observable, Observer, Subscription}
import spray.json._

case class UserToken(userId:Long, token:String)

class BackendRegistration(preferences:Preferences)(implicit ctx:Context) extends DefaultJsonProtocol {

  private val BackendURL = "http://192.168.0.17:9000/v1/auth/signUp"
  val GCMToken = "GCM_Token"
  val Tag = "Registration"
  val PlayServicesResolutionRequest = 9000
  val GoogleServiceConnectionFailure = "GoogleServiceConnectionFailure"
  val GoogleServiceConnectionUnresolvableFailure = "GoogleServiceConnectionUnresolvableFailure"
  val RegistrationTag = "Registration"

  implicit val itsJsonFormat = jsonFormat[Long, String, UserToken](
    UserToken.apply, "userId", "token"
  )

  def register(gcmToken: String, phone: String) =
    Observable.create {
      (observer: Observer[String]) => {
        observer.onNext(HttpRequest
          .post(BackendURL)
          .header("Content-Type", "application/json")
          .send( s"""{"login":"$phone","password":"$gcmToken","extId":"$phone"}""".getBytes)
          .body
        )
        Subscription()
      }
    }.map(_.parseJson.convertTo[UserToken])
      .execAsync
      .subscribe(
        userToken => {
          preferences.token = userToken.token
          preferences.userId = userToken.userId
          new AlertDialog.Builder(ctx)
            .setMessage(R.string.backend_registration_succeeded)
            .show()
        },
        throwable => {
          Log.e(RegistrationTag, throwable.getLocalizedMessage)
          new AlertDialog.Builder(ctx)
            .setTitle(R.string.error)
            .setMessage(R.string.backend_registration_failed)
            .show()
        }
      )
}