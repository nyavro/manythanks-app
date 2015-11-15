package com.nyavro.manythanks

import android.widget.ArrayAdapter
import org.scaloid.common._

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

  def contacts() = List("Would", "be", "wrongful")
}
