package com.nyavro.manythanks.messaging

import com.nyavro.manythanks.R
import org.scaloid.common._
import rx.android.schedulers.AndroidSchedulers
import rx.lang.scala.JavaConversions._
import rx.lang.scala.{Observable, Observer, Subscription}
import rx.schedulers.Schedulers

class SendMessageView extends SActivity {

  lazy val nameLabel = new STextView()
  lazy val message = new SEditText()
  lazy val send = new SButton()

  var name = ""
  var globalId = -1L

  onCreate {
    initParams()
    nameLabel.setText(name)
    send.setText(R.string.send)
    send.onClick {
      Observable.create {
        (observer: Observer[Unit]) => {
          observer.onNext(new MessageService(globalId).send(message.getText.toString))
          observer.onCompleted()
          Subscription()
        }
      }
      .observeOn(AndroidSchedulers.mainThread())
      .subscribeOn(Schedulers.io())
      .subscribe(
        u => {},
        throwable => {
          toast(getString(R.string.send_message_error))
          error(throwable.getMessage)
          finish()
        },
        () => finish()
      )
    }
    setContentView(new SVerticalLayout += nameLabel += message += send)
  }
  def initParams() = {
    name = getIntent.getStringExtra(SendMessageView.Name)
    globalId = getIntent.getLongExtra(SendMessageView.GlobalId, -1L)
  }
}

object SendMessageView {
  val Name = "name"
  val GlobalId = "globalId"
}