package com.nyavro.manythanks.register

import android.util.Log
import com.github.kevinsawicki.http.HttpRequest
import spray.json._

case class UserToken(userId:Long, token:String)

/**
 * Sends signUp request with gcmToken and registered phone to backend and receives security token
 */
class BackendRegistration(gcmToken:String, phone:String) extends DefaultJsonProtocol with Registration[UserToken] {

  private val BackendURL = "http://192.168.0.17:9000/v1/auth/signUp"

  private val Tag = "BackendRegistration"

  implicit val UserTokenJsonFormat = jsonFormat[Long, String, UserToken](UserToken.apply, "userId", "token")

  /**
   * Sends signUp request
   * @return Registered UserToken
   */
  def register():UserToken = {
    Log.i(Tag, "BackendRegistration started")
    val res = HttpRequest
      .post(BackendURL)
      .header("Content-Type", "application/json")
      .send( s"""{"login":"$phone","password":"$gcmToken","extId":"$phone"}""".getBytes)
      .body
      .parseJson
      .convertTo[UserToken]
    Log.i(Tag, s"Backend token: ${res.token} userId: ${res.userId}")
    res
  }
}