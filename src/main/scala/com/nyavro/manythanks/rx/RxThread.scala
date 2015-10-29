package com.nyavro.manythanks.rx

import rx.android.schedulers.AndroidSchedulers
import rx.lang.scala.JavaConversions._
import rx.lang.scala.Observable
import rx.schedulers.Schedulers

import scala.language.implicitConversions

class RxThread[T](observable:Observable[T]) {
  def execAsync = {
    observable
      .subscribeOn(Schedulers.newThread())
      .observeOn(AndroidSchedulers.mainThread())
  }
}

object RxThread {
  implicit def Observable2RxThread[T](observable: Observable[T]): RxThread[T] = new RxThread(observable)
}
