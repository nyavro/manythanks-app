package com.nyavro.manythanks.contacts

import android.database.Cursor
import android.util.Log
import com.nyavro.manythanks.{Contacts, R}
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

  lazy val adapter = new ContactAdapter(R.layout.contact_list_item)

  onCreate {
    list.setAdapter(adapter)
    setContentView(new SVerticalLayout += list)
    adapter.setNotifyOnChange(true)
    Observable.create {
      (observer: Observer[List[DisplayContact]]) => {
        contacts().grouped(20).foreach (items =>
          observer.onNext(items.map(item => DisplayContact(item._2, List())))
        )
        observer.onCompleted()
        Subscription()
      }
    }
      .observeOn(AndroidSchedulers.mainThread())
      .subscribeOn(Schedulers.io())
      .onBackpressureBuffer
      .subscribe(
        items => adapter.addAll(items:_*),
        throwable => {
          toast(getString(R.string.error_loading_contacts))
          Log.e(Tag, throwable.getMessage)
        },
        () => {
          toast("Complete contacts loading")
        }
      )

  }

  def displayContacts():List[DisplayContact] = {
    val name2phones = contacts().map {case (id, name) => (name, phones(id))}
    val globalIds = new ContactsService(name2phones.map(_._2).flatten)
      .globalIds()
      .map(item => item.phone -> item)
      .toMap
    name2phones
      .map {
        case (name, lst) => DisplayContact(name, lst.map(phone => globalIds.get(phone)).flatten)
      }.toList
  }

  def contacts() = {
    val cur = getContentResolver.query(Contacts.CONTENT_URI, null, null, null, s"${Contacts.DISPLAY_NAME} DESC")
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
