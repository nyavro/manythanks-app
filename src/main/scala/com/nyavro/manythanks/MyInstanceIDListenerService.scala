package com.nyavro.manythanks

import android.content.Intent
import com.google.android.gms.iid.InstanceIDListenerService
class MyInstanceIDListenerService extends InstanceIDListenerService {

  override def onTokenRefresh() = startService(new Intent(this, classOf[RegistrationIntentService]))
}
