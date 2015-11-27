package com.nyavro.manythanks.messaging

import android.util.Log
import com.github.kevinsawicki.http.HttpRequest
import spray.json.DefaultJsonProtocol

class MessageService(globalId:Long) extends DefaultJsonProtocol {

  private val BackendURL = "http://192.168.0.17:9000/v1/mark/create"

  private val Tag = "ContactsService"

  def send(text: String) = {
    Log.i(Tag, s"Upping $globalId")
    val mark = s"""{"to":$globalId,"up":true,"message":"$text"}"""
    Log.i(Tag, mark)
    HttpRequest
      .post(BackendURL)
      .header("Content-Type", "application/json")
      .header("Token", "signInSucceessfullToken")
      .send(mark.getBytes)
  }

}
