# Changelog

All notable changes to **CellGuard** will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.0] - 2026-11-09

### Added
- **Overview Dashboard:**
  - Real-time animated Apple-inspired battery filling visualizer with pulse and glow effects.
  - Live hardware metrics: Temperature (°C/°F), Voltage, Inflow/Discharge current, Technology.
  - Transparent hardware capability detection distinguishing hardware-reported, system-reported, and unsupported properties.
  - Rule-based offline smart battery insight cards.
- **Battery Health Screen:**
  - Native iOS-inspired Maximum Capacity retention gauge.
  - Battery condition grading (Excellent, Good, Fair, Degraded, Critical, Insufficient Data).
  - Rated design capacity vs estimated full charge capacity comparison.
  - Android 14+ cycle count detection with transparent calculated fallback.
  - Health confidence scoring based on charge cycle consistency.
  - Interactive "How Health is Calculated" methodology modal.
- **Charging Analytics & History:**
  - Canvas-based hardware-accelerated interactive cubic-bezier line charts with touch scrubber tooltips.
  - Time window filters: 24 Hours, 7 Days, 30 Days, and All Time.
  - Detailed charging session logging (duration, percentage gain, peak temperature, estimated capacity contribution).
- **Settings & Data Sovereignty:**
  - Pure Dark mode, Clean Light mode, and System automatic theme switching.
  - Temperature unit toggle (°C / °F).
  - Custom design capacity verification dialog.
  - Notification alert preferences for high heat (> 41°C), full charge (100%), and low battery (15%).
  - Local CSV data export via system share sheet.
  - One-tap local database erasure.
- **Under the Hood:**
  - 100% offline architecture (zero internet permissions declared).
  - Local Room database with multi-session delta integration estimator.
  - Complete JUnit test coverage for estimation mathematics, safeguards, and insights.
