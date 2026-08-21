<div align="center">

# Mamão com Açúcar

**Collaborative mobile application for mapping public fruit trees across Brazilian cities.**

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0%2B-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-Firestore%20%26%20Auth-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/)
[![OpenStreetMap](https://img.shields.io/badge/OpenStreetMap-OSMDroid-7EBC6F?style=for-the-badge&logo=openstreetmap&logoColor=white)](https://github.com/osmdroid/osmdroid)
[![Android SDK](https://img.shields.io/badge/API-24%2B%20%7C%20Target%2036-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%2B%20StateFlow-F97316?style=for-the-badge)]()

</div>

---

## Overview

**Mamão com Açúcar** is a community-driven Android application built to map and monitor public fruit trees in urban areas across Brazil.

In many Brazilian cities, public streets and parks feature fruit-bearing trees—such as mango, papaya, pitanga, avocado, and jambo—whose harvest often goes unnoticed or spoiled. This project connects local communities by enabling citizens to register tree coordinates, track fruiting stages in real time (blooming, growing, ripe, or out of season), and share updates with neighbor foragers.

Originally created as a React/Vite web prototype, this repository contains the native Android application rebuilt from the ground up using **Kotlin** and **Jetpack Compose**.

---

## Key Features

- **Interactive Map & Tile Sources**: Vector map powered by OSMDroid with support for multiple map styles (CartoDB Voyager, Dark, Positron, and OpenStreetMap Mapnik).
- **Proximity-Based Discovery**: Dynamic tree list sorted by geodesic distance (Haversine formula) relative to the user's current GPS location.
- **Fruiting Lifecycle Tracking**: Community status reports indicating whether trees are blooming, producing green fruit, ripe for picking, or out of season.
- **Collaborative Updates**: Real-time status logs and community comments stored in Cloud Firestore.
- **User Progression**: Gamification system awarding profile badges based on total tree contributions (from *Sementinha* to *Mestre Frutífero*).
- **Edge-to-Edge Interface**: Material Design 3 implementation with adaptive status and navigation bar insets, paired with a custom brand palette (`MamaoOrange` and `MamaoGreen`).

---

## Technical Architecture

The application follows standard Android architecture guidelines using the **MVVM (Model-View-ViewModel)** pattern with unidirectional data flow (UDF).

```
┌─────────────────────────────────────────────────────────┐
│                    Jetpack Compose                      │
│            (Material 3 & Custom Design System)          │
└───────────────────────────┬─────────────────────────────┘
                            │ StateFlow / UI States
┌───────────────────────────▼─────────────────────────────┐
│                      MapViewModel                       │
│             (Business Logic & Location Utilities)       │
└───────────────────────────┬─────────────────────────────┘
                            │ Data Flow
┌───────────────────────────▼─────────────────────────────┐
│                 Firebase / OSMDroid Service             │
│        (Firestore Realtime Sync & Disk Tile Cache)      │
└─────────────────────────────────────────────────────────┘
```

### Tech Stack Specifications

- **Language**: Kotlin 2.0+
- **UI Framework**: Jetpack Compose with Material Design 3
- **Navigation**: Navigation Compose with `ComponentActivity.enableEdgeToEdge()`
- **Map Engine**: OSMDroid (`org.osmdroid:osmdroid-android:6.1.20`) with disk tile caching via `Configuration.getInstance().load()`
- **Backend**: Firebase Auth and Cloud Firestore
- **State Management**: Kotlin `StateFlow` and `collectAsState()`
- **Target SDK**: API 36 (Minimum SDK 24)

---

## Tree Status & Gamification Rules

### Fruiting Cycle (`TreeStatus`)

| Status | Symbol | Description |
| :--- | :---: | :--- |
| **FLOR** | 🌸 | Tree is flowering / blooming. |
| **VERDE** | 🍏 | Fruit is growing, currently green. |
| **PRONTO** | 🍎 | Fruit is ripe and ready for picking. |
| **SECO** | 🍂 | Tree is out of season or without fruit. |

### Contributor Badges (`UserBadge`)

User profiles display progress ranks based on total tree additions recorded in Firestore:

- **Sementinha**: 0 trees registered
- **Brotinho**: 1+ tree registered
- **Cultivador**: 5+ trees registered
- **Protetor da Floresta**: 10+ trees registered
- **Guardião das Frutas**: 25+ trees registered
- **Mestre Frutífero**: 50+ trees registered

---

## Repository Structure

```text
app/src/main/java/com/felipelaurindo/mamaocomacucar/
├── MainActivity.kt                # Main activity initializing edge-to-edge layout & OSMDroid cache
├── MamaoApp.kt                    # NavHost configuration & auth state management
├── data/
│   └── model/
│       ├── LoggedUser.kt          # Authenticated user model
│       ├── Tree.kt                # Tree entry model (coordinates, species, status)
│       ├── TreeStatus.kt          # Status enum & visual metadata
│       ├── TreeUpdate.kt          # Community status update & comment log
│       └── UserBadge.kt           # User badge ranks & contribution logic
├── ui/
│   ├── auth/
│   │   ├── LoginScreen.kt         # Firebase authentication screen
│   │   └── RegisterScreen.kt      # User registration screen
│   ├── map/
│   │   ├── MapScreen.kt           # Main map screen with OSMDroid overlays & FAB controls
│   │   ├── MapViewModel.kt        # Map state management & location handling
│   │   └── components/
│   │       ├── AddTreeDialog.kt   # Dialog to register a tree at selected coordinates
│   │       ├── TreeDetailSheet.kt # BottomSheet showing tree details & update history
│   │       ├── TreeListSheet.kt   # Search, filter, and proximity-sorted list sheet
│   │       └── ToastOverlay.kt    # Native animated notification overlay
│   ├── settings/
│   │   ├── AccountSettingsSheet.kt # User profile, stats & badge display
│   │   └── AppSettingsSheet.kt    # Map tile style selection sheet
│   └── theme/
│       ├── Color.kt               # Color tokens (MamaoOrange, MamaoGreen, Stone palette)
│       ├── Theme.kt               # Material Design 3 theme wrapper
│       └── Type.kt                # Typography configuration
└── util/
    └── LocationUtils.kt           # Geodesic distance calculation (Haversine) & formatters
```

---

## Local Setup & Development

### Prerequisites

- **Android Studio** (Jellyfish / Koala or newer).
- **JDK 17** configured in your environment.
- **Android Device or Emulator** running Android 7.0+ (API 24+) with location services enabled.

### Setup Steps

1. **Clone the repository**:
   ```bash
   git clone https://github.com/felipematheuslo/mamao-com-acucar-android.git
   cd mamao-com-acucar-android
   ```

2. **Configure Firebase**:
   - Download the `google-services.json` file from your Firebase console.
   - Place `google-services.json` in the `app/` directory:
     ```text
     mamao-com-acucar-android/
     └── app/
         └── google-services.json
     ```

3. **Build and Run**:
   - Open the project folder in Android Studio.
   - Wait for Gradle sync to complete.
   - Run the application on your device or emulator (`Shift + F10`).

---

## License

This project is open-source and intended for community use. Contributions, issue reports, and pull requests are welcome.
