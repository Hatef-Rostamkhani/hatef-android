# Hatef Android

The official Android application for [Hatef](https://hatef.ir). The initial release is a small, native **Kotlin WebView shell** with **Firebase Cloud Messaging (FCM)**, designed to evolve into a full native application without changing the Android application ID.

[![Android CI](https://github.com/Hatef-Rostamkhani/hatef-android/actions/workflows/android-ci.yml/badge.svg)](https://github.com/Hatef-Rostamkhani/hatef-android/actions/workflows/android-ci.yml)

## Status

**Bootstrap / MVP.** The APK opens `https://hatef.ir/`, supports Android back navigation, delegates external links to system handlers, and can receive FCM notifications via the native Android SDK. The app deliberately has no JavaScript-to-native bridge.

| Component | Choice |
| --- | --- |
| Application ID | `ir.hatef.app` |
| App language | Kotlin |
| Android minimum | API 24 |
| Android target | API 35 |
| Compile SDK | API 36 |
| UI | Android System WebView |
| Push | Firebase Cloud Messaging |
| Firebase project ID | `hatef-platform` |
| License | Apache License 2.0 (see `LICENSE`) |

## Quick start

1. Install Android Studio, JDK 17, Android SDK Platform 36, and Gradle **9.8.2**.
2. Open this repository as a Gradle project. The Firebase Android configuration is provided in `app/google-services.json`.
3. Build via **Android Studio** or run `gradle :app:assembleDebug`.
4. Install `app/build/outputs/apk/debug/app-debug.apk` on a device with compatible Google Play services.
5. Accept the notification permission on Android 13+.
6. The debug APK displays a **Copy Push ID** button. Once FCM registration succeeds, copy your Firebase Installation ID (FID).
7. For a test push, follow [Firebase setup](docs/FIREBASE.md).

CI installs a pinned Gradle version through the official Gradle GitHub Action, so no machine-wide Gradle installation is required on GitHub runners. A project Gradle Wrapper is not yet included; the pinned version above is required for local CLI builds. Android Gradle Plugin **9.5.0** provides built-in Kotlin support.

## CI and artifacts

GitHub Actions runs Firebase-config validation, Android lint, JVM unit tests, and a debug APK build. The successfully built APK is uploaded as an **Actions artifact** named `hatef-android-debug-apk`. This is a **debug-signed build for testing**, not a release-signed production package. Release signing keys must never be committed.

## Firebase and privacy

`app/google-services.json` contains **public client configuration**, not a privileged service-account credential. It is expected to be packaged in the APK. Keep any Firebase Admin service account JSON and Android signing key outside Git; see [security notes](docs/SECURITY.md).

We use the new **Firebase Installation ID (FID)** registration APIs (`register()` and `onRegistered()`). FID is not a Hatef identity or an authorization token. An authenticated backend device-enrollment endpoint is **not implemented yet**. Until it is available, individual device pushes can be tested using the copied FID and the local Node.js test tool.

## Documentation

- [Architecture and scope](docs/ARCHITECTURE.md)
- [Firebase registration and end-to-end smoke test](docs/FIREBASE.md)
- [Security and privacy notes](docs/SECURITY.md)
- [Development and CI](docs/DEVELOPMENT.md)

## License

Apache License 2.0. See [LICENSE](LICENSE).
