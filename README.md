# CellGuard 🔋
### Premium Android Battery Health & Intelligence Application

> **Understand Your Battery. Protect Its Life.**

CellGuard is an open-source, privacy-first, 100% offline Android application that brings an Apple-inspired Battery Health & Charging intelligence experience to Android devices. It delivers honest capacity estimation, real-time hardware diagnostics, and historical monitoring without cloud backends, accounts, ads, or trackers.

---

## 🌟 Key Features

### 1. Overview Dashboard
- **Animated Battery Visualizer:** Smooth Apple-inspired energy filling animation, dynamic state colors (Green, Orange, Red), charging pulse indicator, and fast-charge detection.
- **Live Hardware Diagnostics:** Real-time readings of battery percentage, temperature, voltage, instantaneous and average current (mA), wattage (W), and cell chemistry.
- **Hardware Interface Support Table:** Transparently reports whether each metric is hardware-reported, system-reported, or unsupported by your device OEM.
- **Smart Battery Insights:** Rule-based, offline, educational advice regarding charging speeds, temperatures, and habits.

### 2. Battery Health & Degradation (iPhone-Inspired)
- **Maximum Capacity Estimation:** Transparent capacity retention estimation relative to factory new (e.g. `94% Estimated Capacity`).
- **Condition Grading:** Clear status categories (`Excellent`, `Good`, `Fair`, `Degraded`, `Critical`, `Collecting Data`).
- **Design vs. Estimated Full Capacity:** Side-by-side comparison of original rated capacity against algorithmic estimations.
- **Cycle Count Detection:** Direct reading of Android 14+ (`BatteryManager.EXTRA_CYCLE_COUNT`) or transparent calculation from historical charging cycles.
- **Confidence Scoring:** High, Medium, Low, or Insufficient Data ratings with mathematical justification.
- **Methodology Transparency:** In-app guide explaining the underlying physics and formula.

### 3. Charging Analytics & History
- **Interactive Canvas Charts:** Custom hardware-accelerated cubic-bezier charts with interactive touch scrubbing and tooltips.
- **Historical Trends:** Track battery percentage, operating temperatures, and voltage discharge curves across 24 Hours, 7 Days, 30 Days, or All Time.
- **Charging Session Logs:** Automatic logging of every charging session (duration, % gained, peak temperature, power source, and capacity contribution).

### 4. Settings & Data Sovereignty
- **Themes:** Pure Dark theme (`#000000` AMOLED), Clean Light theme, and System default.
- **Units:** Toggle between Celsius (°C) and Fahrenheit (°F).
- **Custom Capacity Verification:** Verify or customize your manufacturer's rated design capacity.
- **Safety Notifications:** Optional local alerts for elevated temperature (> 41°C), full charge (100%), and low battery (15%).
- **Data Export:** Instant CSV export via the Android system share sheet.
- **Local Data Erasure:** Full one-tap local database reset.

---

## 🔒 Privacy & Offline Architecture

- **Zero Internet Permissions:** `android.permission.INTERNET` is not included in the manifest. The OS prevents any network activity.
- **No Cloud Servers:** All computations run 100% on-device.
- **No Analytics or Trackers:** Zero Google Analytics, Firebase, Facebook SDK, or ad networks.
- **Local Storage:** All metrics persist strictly in an internal SQLite Room database on your device.

---

## 🔬 Battery Estimation Methodology

Unlike iOS, which interfaces with a proprietary Apple Battery Management System (BMS), Android does not have a universal OEM battery degradation API. CellGuard provides an honest, mathematically sound estimation:

1. **Hardware Charge Counter:** On supported kernels, CellGuard reads `BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER` ($\mu\text{Ah}$) at the start and conclusion of a charging session.
2. **Delta Integration Formula:**
   $$\text{Estimated Full Capacity (mAh)} = \left(\frac{\Delta Q}{\Delta P}\right) \times 100$$
   where $\Delta Q$ is the charge injected in mAh, and $\Delta P$ is the percentage delta ($P_{end} - P_{start}$).
3. **Capacity Retention Formula:**
   $$\text{Battery Health (\%)} = \left(\frac{\text{Estimated Full Charge Capacity}}{\text{Verified Design Capacity}}\right) \times 100$$
4. **Safeguards & Rejection Rules:**
   - Sessions with $< 20\%$ gain are discarded to avoid mathematical noise.
   - Counter resets, battery swaps, and negative deltas are flagged and discarded.
   - Outliers outside 40%–125% of rated design capacity are rejected.
   - At least 3 qualifying sessions are required before an estimate is displayed.

---

## 🛠️ Technology Stack

- **Language:** Kotlin 2.2.10
- **UI Framework:** Jetpack Compose with Material 3 customization
- **Architecture:** MVVM + Clean Architecture principles
- **Asynchrony:** Kotlin Coroutines & StateFlow
- **Database:** AndroidX Room 2.7.2 with KSP
- **Preferences:** AndroidX DataStore Preferences 1.1.1
- **Platform APIs:** Android `BatteryManager`, `ACTION_BATTERY_CHANGED`, Notification Channels
- **Build System:** Gradle 9.6.0 with Android Gradle Plugin 9.5.0 (Kotlin DSL)
- **Min SDK:** API 29 (Android 10)
- **Target SDK:** API 36 (Android 15+)

---

## 🚀 Building & Installing

### Prerequisites
- JDK 21 (Eclipse Adoptium recommended)
- Android SDK with Platforms 29–36

### Build Debug APK
```bash
./gradlew assembleDebug
```
Output located at:
`app/build/outputs/apk/debug/app-debug.apk`

### Build Release APK & App Bundle (AAB)
```bash
./gradlew assembleRelease bundleRelease
```
Outputs located at:
- Release APK: `app/build/outputs/apk/release/app-release-unsigned.apk`
- Release AAB: `app/build/outputs/bundle/release/app-release.aab`

### Install on Device via ADB
```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Running Unit Tests
```bash
./gradlew testDebugUnitTest
```

---

## ⚠️ Known Platform Limitations

- **OEM Counter Limitations:** Certain Android manufacturers (e.g. older budget MediaTek or Unisoc devices) disable or report static zeroes for `BATTERY_PROPERTY_CHARGE_COUNTER`. CellGuard indicates "Not available on this device" rather than displaying inaccurate or fabricated metrics.
- **Hardware Charge Limiting:** Android third-party apps cannot physically cut off USB charging power at 80% without root or manufacturer-specific system privileges. CellGuard alerts you via notification when charging goals are met.

---

## 📄 License

CellGuard is released under the [MIT License](LICENSE).
