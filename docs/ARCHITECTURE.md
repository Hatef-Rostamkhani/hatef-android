# Architecture

## Overview

```text
Hatef backend (future authenticated push API)
        |
        | Firebase Admin SDK (service account, server-only)
        v
Google Firebase Cloud Messaging
        |
        | Google Play services
        v
Hatef Android app (Kotlin)
   +-- FirebaseMessagingService (native FCM callbacks)
   +-- Android notification channel and notification tap intent
   +-- Restricted HTTPS WebView -> https://hatef.ir/
```

## What is included

- One Android application ID: `ir.hatef.app`.
- No extra JavaScript bridge; native FCM is completely separate from webpage JavaScript.
- Main-frame navigation stays in the WebView only for `https://hatef.ir` and `https://www.hatef.ir`. Other supported web URLs are opened externally.
- WebView explicitly disables local file access, cleartext traffic, and mixed content.
- Android 13+ notification runtime permission.
- FCM registers the installation using the FID-based `register()` API. `onRegistered()` saves the ID locally.
- Debug-only button copies FID for a local test push.
- Foreground FCM messages are shown via a native notification, when notification permission is granted.
- Background notification payloads are displayed by the Android/FCM integration.
- Relative `path` values in notification messages are validated before navigation.

## Not implemented in v0.1.0

- Authenticated registration and revocation of FID against a Hatef backend.
- User-scoped delivery, inbox syncing, notification preferences, and logout revocation.
- Native OAuth/Google login integration. External browser authentication requires a deliberate App Links and session handoff design.
- Native file uploads, downloads, location, camera and microphone permissions.
- Cryptographic end-to-end encryption and production message signing.
- Production APK/AAB signing, Play Store publication, and app-update infrastructure.

Do not assume a notification has been read merely because FCM returned a message ID.

## Backend API design (future)

Suggested endpoint: `POST /api/v1/mobile/installations`. It must require a valid Hatef Identity session and must bind a Firebase Installation ID to the authenticated user on the **server**, not to a user ID supplied by the client. Refresh on registration change, and unlink on logout. Support multiple devices per user.

Push delivery metadata can reveal device and traffic information to FCM even when application payloads are encrypted. Avoid private message text in push payloads if privacy requirements change.
