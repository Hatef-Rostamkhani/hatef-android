# Development and CI

## Prerequisites

- Android Studio / SDK Manager and Android SDK Platform 36
- Build Tools 35.0.0
- JDK 17
- Gradle 8.11.1 (CI pins it automatically)
- Python 3 for Firebase config validation

The repository currently omits a checked-in Gradle Wrapper binary. Install the pinned Gradle version locally, or open the Gradle project using Android Studio. A future maintenance change can add a verified Gradle Wrapper.

## Commands

```bash
python3 tools/verify_firebase_config.py
gradle --no-daemon :app:lintDebug :app:testDebugUnitTest :app:assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

Run unit tests after any changes to the URL allowlist or notification deep-link policy. Verify the release build separately, including signing and minification decisions, before publishing.

## GitHub Actions

Workflow: `.github/workflows/android-ci.yml`.

The pipeline runs on pushes, pull requests and manually. It:

1. Validates Firebase configuration.
2. Installs JDK 17 and Android SDK API 36.
3. Sets up Gradle 8.11.1.
4. Executes Android lint, JVM unit tests and the debug APK build.
5. Uploads the resulting debug APK for installation.

Do not interpret a successful CI build as proof of FCM delivery. Real notification tests require an Android device and a service account with FCM send permissions.

## APK signing

Debug builds use the SDK-generated debug signing key. Before publishing, configure a dedicated release signing keystore through a protected build environment and verify release signing with `apksigner`. Never commit release signing keys to the repository.

## Version policy

Keep the permanent Android application ID `ir.hatef.app`. Future device-specific/native functionality can be added without changing this identity, provided signing continuity is maintained.
