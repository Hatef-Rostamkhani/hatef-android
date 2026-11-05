# Development and CI

## Prerequisites

- Android Studio / SDK Manager and Android SDK Platform 36
- Build Tools 35.0.0
- JDK 17
- Gradle 9.8.2 (pinned in CI)
- Android Gradle Plugin 9.5.0 with built-in Kotlin (no separate Kotlin Android plugin)
- Python 3 for Firebase config validation

The repository currently omits a checked-in Gradle Wrapper binary. Install Gradle 9.8.2 locally, or open the Gradle project using Android Studio. A future maintenance change can add a verified Gradle Wrapper.

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
2. Uses an explicit Ubuntu 24.04 runner instead of the moving `ubuntu-latest` target.
3. Installs JDK 17 and Android SDK API 36.
4. Sets up Gradle 9.8.2 and Android Gradle Plugin 9.5.0 with built-in Kotlin.
5. Executes Android lint, JVM unit tests and the debug APK build.
6. Uploads the resulting debug APK for installation.

GitHub's checkout v7, setup-java v6 and upload-artifact v7 use Node.js 24, avoiding Node.js 20 deprecation notices. Gradle's action annotates unsupported/outdated versions; Gradle 9.8.2 is selected to keep the toolchain current.

The Android application ID remains `ir.hatef.app`, with `minSdk = 24`. These CI and toolchain upgrades are not intended to change app functionality.

Do not interpret a successful CI build as proof of FCM delivery. Real notification tests require an Android device and a service account with FCM send permissions.

## APK signing

Debug builds use the SDK-generated debug signing key. Before publishing, configure a dedicated release signing keystore through a protected build environment and verify release signing with `apksigner`. Never commit release signing keys to the repository.

## Version policy

Keep the permanent Android application ID `ir.hatef.app`. Future device-specific/native functionality can be added without changing this identity, provided signing continuity is maintained.
