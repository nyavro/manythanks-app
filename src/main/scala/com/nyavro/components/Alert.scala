package com.nyavro.components

import android.app.AlertDialog.Builder
import android.content.DialogInterface.OnClickListener
import android.content.{Context, DialogInterface}
import com.nyavro.manythanks.R

class Alert(title:String, message:Option[String])(implicit ctx:Context) {
  def run(onApprove: ()=>Unit, onDecline: ()=>Unit = () => {}) = {
    message.map(msg => new Builder(ctx).setMessage(msg)).getOrElse(new Builder(ctx))
      .setIcon(android.R.drawable.alert_light_frame)
      .setTitle(title)
      .setPositiveButton(
        R.string.dialog_ok,
        new OnClickListener() {
          override def onClick(dialog: DialogInterface, whichButton: Int) = onApprove()
        }
      )
      .setNegativeButton(
        R.string.dialog_cancel,
        new OnClickListener {
          override def onClick(dialog: DialogInterface, whichButton: Int) = onDecline()
        }
      )
      .create()
      .show()
  }
}

object Alert {
  def apply(titleId:Int, messageId:Option[Int] = None)(implicit ctx:Context):Alert = Alert(ctx.getString(titleId), messageId.map(ctx.getString))
  def apply(title:String, message:Option[String])(implicit ctx:Context):Alert = new Alert(title, message)
}

