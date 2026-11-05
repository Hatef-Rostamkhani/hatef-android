package ir.hatef.app

import android.content.Context

/**
 * FID identifies the installation, not the signed-in Hatef user.
 * Never treat it as an authentication credential.
 */
object PushRegistrationStore {
    private const val PREFS = "hatef_push"
    private const val FID = "firebase_installation_id"

    fun save(context: Context, fid: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(FID, fid).apply()
        // TODO: Send FID to a future authenticated Hatef backend endpoint.
        // The server must bind FID to the current session, revoke it on logout,
        // and handle re-registration. Never send a user ID supplied by the client
        // without validating the authenticated session.
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().remove(FID).apply()
    }

    fun current(context: Context): String? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(FID, null)
}
