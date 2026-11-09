# CellGuard Privacy Charter

**Last updated:** November 2026

CellGuard is engineered with a strict **Privacy-First, Offline-Only** architecture. Your device battery is your personal hardware, and your charging habits belong only to you.

---

## 1. Zero Cloud Transmission
- **No Internet Permission:** CellGuard's `AndroidManifest.xml` does not declare the `android.permission.INTERNET` permission. The operating system prevents the application from making any network calls.
- **No External Servers:** CellGuard operates entirely on-device without any cloud backend, telemetry servers, or remote endpoints.

## 2. No Analytics, Trackers, or Ads
- **Zero Third-Party SDKs:** The application does not contain Google Firebase, Google Analytics, Meta SDK, crash reporting trackers, or advertising networks.
- **Zero Identity Collection:** No accounts, no sign-in, no passwords, no email collection, no device IDs, and no advertising identifiers (GAID).

## 3. Sandboxed Local Storage
- **Room Database:** All recorded metrics, charging session durations, and estimated degradation parameters are stored exclusively in an encrypted private SQLite database inside the app's sandboxed storage directory.
- **User Control:** You can permanently erase all historical data at any time via **Settings → Clear Local Battery Data**.
- **Data Export:** When exporting your data in CSV format, files are generated locally and dispatched only via your system share sheet to destinations you explicitly authorize.

## 4. Transparent Hardware Permissions
- CellGuard requests only essential system hardware broadcasts:
  - `ACTION_BATTERY_CHANGED`: Reads battery percentage, charging current, voltage, and temperature from Android's standard BatteryManager API.
  - `POST_NOTIFICATIONS`: (Optional, Android 13+) Used strictly for local alerts (such as high-temperature warnings or full charge reminders) created by the local device operating system.
- CellGuard **never** requests sensitive permissions such as Location, Camera, Contacts, Microphone, File System access, or Accessibility services.
