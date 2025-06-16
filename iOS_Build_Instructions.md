# iOS Build Instructions for Pigs Tally

## Prerequisites
1. **macOS** with Xcode installed
2. **Xcode Command Line Tools** installed
3. **Kotlin Multiplatform Mobile plugin** for Xcode (optional but recommended)

## Method 1: Build from Terminal

### Step 1: Fix Gradle Wrapper (if needed)
```bash
# Navigate to project root
cd /Users/markduenas/development/ai/pigs

# Re-download Gradle wrapper (if corrupted)
./gradlew wrapper --gradle-version 8.5

# Or download manually from https://gradle.org/releases/
```

### Step 2: Build iOS Framework
```bash
# Build for iOS Simulator (Apple Silicon)
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64

# Build for iOS Simulator (Intel)
./gradlew :composeApp:linkDebugFrameworkIosX64

# Build for iOS Device
./gradlew :composeApp:linkDebugFrameworkIosArm64
```

### Step 3: Create iOS App Project
Since there's no iOS app project yet, you need to create one:

1. **Open Xcode**
2. **Create New Project** → **iOS** → **App**
3. **Product Name**: "Pigs Tally"
4. **Bundle Identifier**: "com.markduenas.pigstally"
5. **Language**: Swift
6. **Interface**: SwiftUI

### Step 4: Integrate Framework
1. **Add Framework**: Drag the built `ComposeApp.framework` to your Xcode project
   - Framework location: `composeApp/build/bin/iosSimulatorArm64/debugFramework/`
2. **Import in ContentView.swift**:
```swift
import SwiftUI
import ComposeApp

struct ContentView: View {
    var body: some View {
        ComposeUIViewController()
            .ignoresSafeArea(.all)
    }
}

struct ComposeUIViewController: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

## Method 2: Use KMM Wizard (Recommended)

### Step 1: Install KMM Plugin
```bash
# Install Kotlin Multiplatform Mobile plugin for Xcode
# From Xcode: Xcode → Preferences → Components → Download
```

### Step 2: Generate iOS App
```bash
# Use KMM wizard to generate iOS app template
./gradlew :composeApp:createXcodeProject
```

## Method 3: Manual iOS App Creation

### Create iosApp directory structure:
```
iosApp/
├── iosApp/
│   ├── ContentView.swift
│   ├── iOSApp.swift
│   └── Info.plist
├── iosApp.xcodeproj/
│   └── project.pbxproj
└── Configuration/
    └── Config.xcconfig
```

### ContentView.swift:
```swift
import SwiftUI
import shared

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all, edges: .bottom)
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

### iOSApp.swift:
```swift
import SwiftUI

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
```

## Build and Run

### Using Xcode:
1. Open the created iOS project in Xcode
2. Select your target device/simulator
3. Press **⌘+R** to build and run

### Using Command Line:
```bash[iOS_DEVELOPMENT.md](iOS_DEVELOPMENT.md)
# Build for simulator
xcodebuild -project iosApp.xcodeproj -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 16' build

# Run on simulator
xcrun simctl boot "iPhone 16"
xcrun simctl install booted /Users/markduenas/Library/Developer/Xcode/DerivedData/iosApp-arbligzseibmfhgtxixtsbchwcuf/Build/Products/Debug-iphonesimulator/iosApp.app
xcrun simctl launch booted com.markduenas.pigstally
```

## Troubleshooting

### Common Issues:
1. **Framework not found**: Ensure framework is built before opening Xcode
2. **Architecture mismatch**: Build correct framework for your target (arm64 for device, x64/arm64 for simulator)
3. **Missing dependencies**: Check that all Kotlin dependencies are properly configured

### Clean Build:
```bash
./gradlew clean
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

## App Icon Setup
Follow the instructions in `iOS_Icon_Instructions.md` to add the Leaning Jowler app icon to your iOS project.

## Next Steps
Once the iOS app is running:
1. Test all game functionality
2. Add app icons using the provided designs
3. Test on physical iOS device
4. Submit to App Store (if desired)