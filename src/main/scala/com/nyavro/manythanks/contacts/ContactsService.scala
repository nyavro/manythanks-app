package com.nyavro.manythanks.contacts

import android.util.Log
import com.github.kevinsawicki.http.HttpRequest
import spray.json._

case class UserToken(userId:Long, token:String)

/**
 * Requests global ids of contacts by phone
 */
class ContactsService extends DefaultJsonProtocol with CollectionFormats {

  private val BackendURL = "http://192.168.0.17:9000/v1/contact/list"

  private val Tag = "ContactsService"

  implicit val GlobalIdsFormat = jsonFormat[Long, String, Contact](Contact.apply, "id", "phone")

  case class Res(list:List[Contact])

  implicit val ResFormat = jsonFormat[List[Contact], Res](Res.apply, "list")

  def format(phones: Iterable[String]) = s"""{"extIds":["${phones.mkString("\",\"")}"]}"""

  /**
   * Sends request
   * @return Registered UserToken
   */
  def globalIds(phones:Iterable[String]):List[Contact] = {
    Log.i(Tag, "Requesting global ids")
    val form = format(phones)
    Log.i(Tag, form)
    val json: JsValue = HttpRequest
      .post(BackendURL)
      .header("Content-Type", "application/json")
      .header("Token", "signInSucceessfullToken")
      .send(form.getBytes)
      .body
      .parseJson
    json.convertTo[Res].list
  }
}