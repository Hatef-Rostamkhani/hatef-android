# Security and privacy

## Configuration vs secrets

`app/google-services.json` contains public Android Firebase client configuration: project number, Firebase app ID, and a client API key. Since these values are embedded in the distributed APK, they are not a privileged security boundary. Restrict the Firebase API key appropriately at Google Cloud and audit enabled APIs and Firebase Security Rules.

**Never commit:** Firebase Admin service account credentials, private keys, signing keystores, signing passwords, access tokens, or user data. Keep release signing credentials in a dedicated secrets manager or protected CI secrets with restricted access.

## Native/WebView separation

- JavaScript is enabled because the web application needs it, but **no JavaScript bridge** is exposed.
- Only allowlisted HTTPS origins render in WebView; arbitrary remote links open the system browser.
- No HTTP, WebView file or content access, or mixed content.
- Notification navigation accepts only relative paths. Never load an untrusted URL from an FCM data payload.
- Validate any future API callbacks in the native layer. Web content must not be trusted to choose a logged-in user identity.

## Authentication

An FID identifies an installation, not a user. Never authorize a user operation on the basis of an FID. The future device registration API must use existing Hatef Identity session validation, link installations to the authenticated user, and revoke associations on logout.

Firebase push transport is not end-to-end encryption: payload confidentiality from Google requires application-layer encryption. Analytics configuration and FCM routing metadata are separate from encrypted content.

## Known limitations

Android WebView is not a drop-in replacement for full Chrome. OAuth providers may block embedded user-agent sign-in. External sign-in, file chooser, downloads, and permission-sensitive web APIs need explicit future implementation and device testing. Firebase/Google service access requires connectivity to Google.
