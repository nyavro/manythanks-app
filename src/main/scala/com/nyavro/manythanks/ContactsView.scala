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
    val projection = Array(Contacts.DISPLAY_NAME, Contacts.NUMBER)
    val cur = getContentResolver.query(Contacts.CONTENT_URI, null, null, null, null)
    val nameIndex = cur.getColumnIndex(Contacts.DISPLAY_NAME)
    val idIndex = cur.getColumnIndex(Contacts.NUMBER)

    toList(cur) {
      cur => {
        cur.getString(nameIndex) + " " + cur.getString(idIndex)
      }
    }
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
