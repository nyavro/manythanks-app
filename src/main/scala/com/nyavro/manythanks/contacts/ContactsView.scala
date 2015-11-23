package com.nyavro.manythanks.contacts

import android.database.Cursor
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
      (observer: Observer[DisplayContact]) => {
        displayContacts().foreach (item => {
            Thread.sleep(10)
            observer.onNext(item)
          }
        )
        Subscription()
      }
    }
      .observeOn(AndroidSchedulers.mainThread())
      .subscribeOn(Schedulers.io())
      .subscribe(
        item => adapter.add(item)
      )
  }

  def displayContacts():List[DisplayContact] = {
    val name2phones = contacts().map {case (id, name) => (name, phones(id))}.toMap
    val localIds = name2phones.values.flatten
    val globalIds = new ContactsService(localIds).globalIds().map(item => item.phone -> item).toMap
    name2phones
      .map {
        case (name, lst) => DisplayContact(name, lst.map(phone => globalIds.get(phone)).flatten)
      }.toList
  }

  def contacts() = {
    val cur = getContentResolver.query(Contacts.CONTENT_URI, null, null, null, s"${Contacts.DISPLAY_NAME} ASC")
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
