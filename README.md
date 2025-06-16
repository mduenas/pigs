# Pass the Pigs - Mobile App

A Kotlin Multiplatform Compose mobile application for keeping score in the classic dice game "Pass the Pigs".

## Features

- **Cross-platform**: Runs on both Android and iOS using Kotlin Multiplatform
- **Intuitive Scoring**: Large, easily identifiable buttons for all pig positions
- **Smart Player Management**: Quick player setup with optional custom names
- **Turn Management**: Automatic turn switching and game flow
- **Game Rules**: Full implementation of official Pass the Pigs scoring
- **Persistent State**: Game state is automatically saved and restored

## Game Rules

Pass the Pigs is played by rolling two plastic pig-shaped dice. Players score points based on how the pigs land:

### Scoring Positions

- **Trotter**: 5 points (pig on all four feet)
- **Razorback**: 5 points (pig on back, legs up)
- **Snouter**: 10 points (pig on snout and front legs)
- **Leaning Jowler**: 15 points (pig on jowl, ear, and front foot)
- **Double positions**: 2x, 4x, or 6x points when both pigs land the same way
- **Mixed combinations**: Add individual scores together

### Penalties

- **Pig Out**: Both pigs on opposite sides - lose turn score
- **Oinker**: Both pigs touching - total game score reset to 0
- **Piggyback**: One pig on top of other - player eliminated

### Winning

First player to reach 100 points wins the game!

## Building the App

### Prerequisites

- Android Studio with Kotlin Multiplatform plugin
- JDK 11 or higher
- Gradle 8.2+
- For iOS: Xcode and iOS SDK

### Android

```bash
gradle :composeApp:assembleDebug
```

The APK will be generated at: `composeApp/build/outputs/apk/debug/composeApp-debug.apk`

### iOS

```bash
gradle :shared:compileKotlinIosArm64
gradle :shared:compileKotlinIosSimulatorArm64
```

For full iOS app, open in Xcode and build the iOS target.

### Build Status

✅ **Android** - Builds successfully  
✅ **iOS** - Compiles successfully  
✅ **Shared Logic** - All targets compile

## Project Structure

```
PassThePigs/
├── shared/                     # Shared KMP module
│   ├── src/commonMain/
│   │   ├── kotlin/com/markduenas/pigstally/
│   │   │   ├── model/         # Data models
│   │   │   ├── game/          # Game logic and ViewModel
│   │   │   ├── ui/            # Compose UI components
│   │   │   └── storage/       # Persistence layer
│   ├── src/androidMain/       # Android-specific implementations
│   └── src/iosMain/          # iOS-specific implementations
├── composeApp/                # Main application module
│   ├── src/androidMain/      # Android app entry point
│   └── src/commonMain/       # Shared app logic
└── gradle/                   # Build configuration
```

## Technical Features

- **MVVM Architecture**: Clean separation of concerns
- **Reactive UI**: State-driven Compose UI with coroutines
- **Cross-platform Storage**: SharedPreferences (Android) / UserDefaults (iOS)
- **Material 3 Design**: Modern, accessible interface
- **Responsive Layout**: Optimized for different screen sizes

## Accessibility

- Large touch targets (minimum 44dp/44pt)
- High contrast colors
- Screen reader support
- Scalable fonts

## License

This project is open source. The Pass the Pigs game concept is owned by Winning Moves.