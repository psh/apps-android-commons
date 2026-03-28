package fr.free.nrw.commons.auth

import android.accounts.AbstractAccountAuthenticator
import android.content.Intent
import android.os.IBinder
import android.app.Service

/**
 * Handles the Auth service of the App, see AndroidManifests for details
 */
class WikiAccountAuthenticatorService : Service() {
    private var authenticator: AbstractAccountAuthenticator? = null

    override fun onCreate() {
        super.onCreate()
        authenticator = WikiAccountAuthenticator(this)
    }

    override fun onBind(intent: Intent): IBinder? =
        authenticator?.iBinder
}
