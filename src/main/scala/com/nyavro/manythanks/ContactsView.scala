package com.nyavro.manythanks

import android.database.Cursor
import android.widget.ArrayAdapter
import org.scaloid.common._

import scala.annotation.tailrec

class ContactsView extends SActivity {

  val Tag = "ContactsView"

  lazy val preferences = new Preferences(defaultSharedPreferences)

  lazy val list = new SListView


  onCreate {
    list.setAdapter(
      new ArrayAdapter[String](
        this,
        android.R.layout.simple_list_item_1,
        contacts().toArray
      )
    )
    setContentView(new SVerticalLayout += list)
  }

  def contacts() = {
    val projection = Array(Contacts.DISPLAY_NAME)
    val cur = getContentResolver.query(Contacts.CONTENT_URI, projection, null, null, null)
    val index = cur.getColumnIndex(Contacts.DISPLAY_NAME)
    cur.moveToFirst()
    toList(cur) {
      cur => cur.getString(index)
    }
  }

  @tailrec
  private def toList[A](cursor: Cursor, acc:List[A] = Nil)(convert: Cursor => A): List[A] = {
    if (cursor.isAfterLast) acc
    else {
      val item = convert(cursor)
      cursor.moveToNext
      toList(cursor, item::acc)(convert)
    }
  }
}
