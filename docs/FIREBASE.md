# Firebase Cloud Messaging setup

## Firebase project

The supplied `app/google-services.json` configures the following public Android Firebase client:

- Firebase project ID: `hatef-platform`
- Android application ID: `ir.hatef.app`
- SDK: `com.google.firebase:firebase-messaging` using the Firebase Android BoM

Do not confuse the Firebase client configuration with Firebase Admin credentials. The Android client does **not** need Firebase Authentication for FCM registration.

## Test installation registration

1. Build and install the debug APK from Android Studio or the Android CI artifact.
2. Launch the app on a device with Google Play services and internet access.
3. Grant the notification permission when asked (Android 13+).
4. Tap **Copy Push ID** at the bottom of the debug build. If the ID is unavailable, check connectivity and FCM logs.
5. Keep the copied Firebase Installation ID (FID) private. It identifies a single app installation, not a Hatef account.

FCM's current Android API supports `FirebaseMessaging.register()` and `FirebaseMessagingService.onRegistered(fid)`. Older registration-token APIs are deprecated. The test sender uses the recommended Admin SDK `fid` field.

## Send a test message from your own machine/server

Create a **server-side** Firebase/Google Cloud service account that has the permission to send Firebase Cloud Messaging messages. Store its JSON securely. Never add it to Git, embed it in an APK, or expose it through a WebView.

```bash
cd tools
npm install
export GOOGLE_APPLICATION_CREDENTIALS="/secure/path/service-account.json"
export HATEF_DEVICE_FID="PASTE_COPIED_FID_HERE"
npm run send
```

An example notification is sent with a `path` of `/notifications`. When tapped, the app validates the path and loads `https://hatef.ir/notifications`.

A successful send returns an FCM message identifier, **not** proof of receipt, display, or user interaction.

## Test cases

- Foreground: app visible; native foreground handler presents a notification.
- Background: app switched away; Android/FCM presents a notification payload.
- Tap: opens the app and the validated relative path.
- Denied permission: no normal visible notifications.
- No internet or no compatible Google Play services: FCM delivery is unavailable.
- Force-stopped app: delivery is not guaranteed.
- Long idle/Doze: delivery may be deferred.

If the backend later sends data-only messages, test foreground and background delivery separately and use only user-visible high-priority messages according to FCM guidance.

## Documentation

- [Firebase Android setup](https://firebase.google.com/docs/android/setup)
- [FCM Android client registration](https://firebase.google.com/docs/cloud-messaging/android/get-started)
- [FCM Android receiving messages](https://firebase.google.com/docs/cloud-messaging/android/receive-messages)
- [Firebase Admin SDK sending](https://firebase.google.com/docs/cloud-messaging/send/admin-sdk)
