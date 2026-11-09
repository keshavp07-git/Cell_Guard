# Contributing to CellGuard

Thank you for your interest in contributing to CellGuard! We welcome contributions that maintain our high standards of mathematical correctness, battery efficiency, and iOS-inspired minimalist design.

---

## Code of Conduct
Please be respectful, collaborative, and constructive when discussing features, bug reports, and pull requests.

## Core Engineering Principles
1. **Never Fabricate Data:** Never display guessed, simulated, or placeholder metrics as true hardware readings in production. If a metric is not exposed by an OEM driver, report it honestly as "Not available on this device".
2. **Offline-First:** Do not add network dependencies, cloud APIs, or external telemetry libraries.
3. **Battery Efficiency:** Avoid continuous background polling loops or heavy wake-locks. Rely on event-driven broadcasts (`ACTION_BATTERY_CHANGED`) and minimal scheduled WorkManager/DataStore persistence.

## Development Setup
1. Clone repository:
   ```bash
   git clone https://github.com/your-org/cellguard.git
   cd cellguard
   ```
2. Open in Android Studio Iguana+ or compile via Gradle:
   ```bash
   ./gradlew assembleDebug
   ```
3. Run unit tests before submitting:
   ```bash
   ./gradlew testDebugUnitTest
   ```

## Pull Request Guidelines
- Follow modern Kotlin idioms and Jetpack Compose state-management standards.
- Add unit tests for any modifications to `BatteryHealthEstimator` or `SmartInsightEngine`.
- Ensure all screens conform to the Apple-inspired Design System (`AppleCard`, `InteractiveChart`, `MetricRow`).
