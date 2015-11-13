package com.nyavro.manythanks

import android.content.Intent
import com.google.android.gms.iid.InstanceIDListenerService
import com.nyavro.manythanks.register.RegistrationService

class LocalInstanceIDListenerService extends InstanceIDListenerService {

  override def onTokenRefresh() = startService(new Intent(this, classOf[RegistrationService]))
}
