package com.nyavro.manythanks

import android.database.Cursor
import android.widget.ArrayAdapter
import org.scaloid.common._
import rx.android.schedulers.AndroidSchedulers
import rx.lang.scala.JavaConversions._
import rx.lang.scala.{Observable, Observer, Subscription}
import rx.schedulers.Schedulers

import scala.annotation.tailrec

class ContactsView extends SActivity {

  val Tag = "ContactsView"

  lazy val preferences = new Preferences(defaultSharedPreferences)

  lazy val list = new SListView

  lazy val adapter = new ArrayAdapter[String](
    this,
    android.R.layout.simple_list_item_1
  )

  onCreate {
    list.setAdapter(adapter)
    setContentView(new SVerticalLayout += list)
    adapter.setNotifyOnChange(true)
    Observable.create {
      (observer: Observer[(String, List[String])]) => {
        contacts().foreach {
          case (id, name) => observer.onNext(name, phones(id))
        }
        Subscription()
      }
    }
      .observeOn(AndroidSchedulers.mainThread())
      .subscribeOn(Schedulers.io())
      .subscribe(
        res => {
          adapter.add(res._1 + res._2.mkString(";"))
        }
      )
  }

  def contacts() = {
    val cur = getContentResolver.query(Contacts.CONTENT_URI, null, null, null, null)
    val nameIndex = cur.getColumnIndex(Contacts.DISPLAY_NAME)
    val idIndex = cur.getColumnIndex(Contacts.ID)
    val hasPhoneIndex = cur.getColumnIndex(Contacts.HAS_PHONE_NUMBER)
    toList(cur) {
      cur => if(cur.getString(hasPhoneIndex).toInt > 0) Some((cur.getString(idIndex), cur.getString(nameIndex))) else None
    }.flatten
  }

  def phones(contactId:String) = {
    val cur = getContentResolver.query(Contacts.PHONE_URI, null, s"${Contacts.PHONE_ID} = ?", Array(contactId), null)
    val phoneIndex = cur.getColumnIndex(Contacts.NUMBER)
    toList(cur) { cur => cur.getString(phoneIndex) }
  }

  private def toList[A](cursor: Cursor)(convert: Cursor => A): List[A] = {
    @tailrec
    def toList(acc:List[A] = Nil): List[A] =
      if (cursor.isAfterLast) {
        cursor.close()
        acc
      }
      else {
        val item = convert(cursor)
        cursor.moveToNext
        toList(item :: acc)
      }
    cursor.moveToFirst()
    toList()
  }
}
