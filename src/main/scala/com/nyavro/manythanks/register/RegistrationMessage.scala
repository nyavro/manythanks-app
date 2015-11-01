package com.nyavro.manythanks.register

class RegistrationMessage(phone:String, registrationId:Long) {

  def value = s"${RegistrationMessage.prefix}:$phone:${new NarrowedSpace(phone.hashCode + ((registrationId%1000)*1001).toInt).value}"

  def fit(message:String) = value.equals(message)
}

class NarrowedSpace(original:Int) {
  private val spaceFactor: Int = 899981
  val value = original.hashCode() % spaceFactor
}

object RegistrationMessage {
  val prefix = "Registration"
}