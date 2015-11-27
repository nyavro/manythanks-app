package com.nyavro.manythanks.contacts

import android.content.{Context, Intent}
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{ArrayAdapter, TextView, Toast}
import com.nyavro.manythanks.R
import com.nyavro.manythanks.messaging.SendMessageView

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
//    val hasContact = view.findViewById(R.id.has_contact).asInstanceOf[CheckBox]
    val displayName = view.findViewById(R.id.display_name).asInstanceOf[TextView]
    val item = getItem(position)
//    hasContact.setChecked(contact.contacts.nonEmpty)
    displayName.setText(item.name)
    view.setOnClickListener(
      new OnClickListener {
        override def onClick(v: View) = {
          item.contacts.headOption.map { contact => {
            val intent = new Intent(context, classOf[SendMessageView])
            intent.putExtra(SendMessageView.Name, item.name)
            intent.putExtra(SendMessageView.GlobalId, contact.id)
            context.startActivity(intent)
          }
          }
        }
      }
    )
    view
  }

  override def isEnabled(position:Int) = false
}
