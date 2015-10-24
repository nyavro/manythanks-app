package com.nyavro.manythanks.register

import android.content.{Context, Intent}
import android.util.Log
import com.github.kevinsawicki.http.HttpRequest
import com.google.android.gms.common.{ConnectionResult, GoogleApiAvailability}
import org.scaloid.common.Preferences
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

case class UserToken(userId:Long, token:String)

class Registration(preferences:Preferences)(implicit ctx:Context) extends DefaultJsonProtocol {

  def register(gcmToken: String, phone:String) = Future {
    parse(
      HttpRequest
        .post("http://192.168.0.17:9000/v1/auth/signUp")
        .header("Content-Type", "application/json")
        .send(s"""{"login":"$phone","password":"$gcmToken","extId":"$phone"}""".getBytes)
        .body
    ).map {
      userToken =>
        preferences.token = userToken.token
        preferences.userId = userToken.userId
    }
  }

  val GCMToken = "GCM_Token"
  val Tag = "Registration"
  val PlayServicesResolutionRequest = 9000
  val GoogleServiceConnectionFailure = "GoogleServiceConnectionFailure"
  val GoogleServiceConnectionUnresolvableFailure = "GoogleServiceConnectionUnresolvableFailure"
  val RegistrationTag = "Registration"

  def check() = {
  }

  implicit val itsJsonFormat = jsonFormat[Long, String, UserToken](
    UserToken.apply, "userId", "token"
  )

  def parse(response:String):Option[UserToken] =
    Try{
      response.parseJson.convertTo[UserToken]
    } match {
      case Success(x) => Some(x)
      case Failure(x) => Log.e("Registration", x.getLocalizedMessage); None
    }

  def getGcmRegistration(phone:String) = {
    if(!isPlayServicesAvailable.isDefined) {
      val intent = new Intent(ctx, classOf[RegistrationIntentService])
      intent.putExtra(Registration.PhoneExtra, phone)
      ctx.startService(intent)
    }
  }

  def isPlayServicesAvailable:Option[String] = {
    val availability = GoogleApiAvailability.getInstance()
    val result = availability.isGooglePlayServicesAvailable(ctx)
    if (result != ConnectionResult.SUCCESS)
      Some(
        if (availability.isUserResolvableError(result))
          GoogleServiceConnectionFailure
        else
          GoogleServiceConnectionUnresolvableFailure
      )
    else
      None
  }
}

object Registration {
  val PhoneExtra = "Phone"
}
