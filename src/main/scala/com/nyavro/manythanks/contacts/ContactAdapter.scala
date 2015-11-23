package com.nyavro.manythanks.contacts

import android.content.Context
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{ArrayAdapter, CheckBox, TextView}
import com.nyavro.manythanks.R

class ContactAdapter(resource:Int)(implicit context: android.content.Context) extends ArrayAdapter[DisplayContact](context, resource) {

  def ensureView(view:View, group:ViewGroup) = view match {
    case null =>
      getContext
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE)
        .asInstanceOf[LayoutInflater]
        .inflate(resource, group, false)
    case _ => view
  }

  override def getView(position:Int, v:View, parent:ViewGroup):View = {
    val view = ensureView(v, parent)
    val hasContact = view.findViewById(R.id.has_contact).asInstanceOf[CheckBox]
    val displayName = view.findViewById(R.id.display_name).asInstanceOf[TextView]
//    if(position <  items.length) {
    val contact = getItem(position)
    hasContact.setChecked(contact.contacts.nonEmpty)
    displayName.setText(contact.name)
//    }
    view
  }

}
