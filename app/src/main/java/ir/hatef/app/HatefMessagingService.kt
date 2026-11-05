package ir.hatef.app

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class HatefMessagingService : FirebaseMessagingService() {
    override fun onRegistered(installationId: String) {
        super.onRegistered(installationId)
        PushRegistrationStore.save(applicationContext, installationId)
        Log.i(TAG, "FCM registration completed")
    }

    override fun onUnregistered(installationId: String) {
        super.onUnregistered(installationId)
        PushRegistrationStore.clear(applicationContext)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.notification?.title
            ?: message.data["title"]
            ?: getString(R.string.notification_default_title)
        val body = message.notification?.body ?: message.data["body"] ?: return
        val path = message.data["path"]
        NotificationHelper.show(this, title, body, path)
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        Log.w(TAG, "FCM messages deleted; backend synchronization is required")
        // TODO: Trigger an authenticated inbox synchronization when available.
    }

    companion object {
        private const val TAG = "HatefFCM"
    }
}
