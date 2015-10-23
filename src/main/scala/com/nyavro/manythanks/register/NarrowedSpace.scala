package com.nyavro.manythanks.register

class NarrowedSpace(original:Int) {
  private val spaceFactor: Int = 899981
  val value = original.hashCode() % spaceFactor
}
