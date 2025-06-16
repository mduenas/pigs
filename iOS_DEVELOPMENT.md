# iOS Development from Android Studio

This document explains how to develop and run the iOS app directly from Android Studio using the configured Gradle tasks and external tools.

## Quick Start

### Method 1: Using Gradle Tasks (Recommended)

Open the Gradle panel in Android Studio and navigate to:
```
PassThePigs > Tasks > ios development
```

**Available Tasks:**
- `iosDev` - Build all frameworks for simulator
- `iosRun` - Build and run iOS app in simulator
- `iosRunQuick` - Quick run (assumes frameworks already built)
- `iosDebugInfo` - Show iOS environment information
- `iosClean` - Clean all iOS build artifacts

### Method 2: Using External Tools

Right-click in the project explorer and select:
```
External Tools > [Tool Name]
```

**Available External Tools:**
- Build iOS App
- Launch iOS Simulator  
- Clean iOS Build
- iOS Debug Info
- List iOS Simulators

## Detailed Usage

### First Time Setup

1. **Check Environment:**
   ```bash
   ./gradlew iosDebugInfo
   ```
   This will verify Xcode installation and show available simulators.

2. **Build Frameworks:**
   ```bash
   ./gradlew iosDev
   ```
   This builds both `shared` and `composeApp` frameworks.

### Daily Development Workflow

#### Option A: Full Build and Run
```bash
./gradlew iosRun
```
This will:
1. Build KMP frameworks
2. Build iOS app via Xcode
3. Launch iOS Simulator
4. You manually launch the app in simulator

#### Option B: Quick Development Cycle
```bash
# First time or after clean
./gradlew iosDev

# Then for subsequent runs
./gradlew iosRunQuick
```

#### Option C: Step by Step
```bash
# 1. Build frameworks
./gradlew iosDev

# 2. Launch simulator
./gradlew iosSimulatorBoot

# 3. Build iOS app
./gradlew iosBuild
```

### Advanced Tasks

#### Clean Everything
```bash
./gradlew iosClean
```

#### Full Rebuild
```bash
./gradlew iosFullBuild
```

#### Build for Physical Device
```bash
./gradlew iosDevDevice
./gradlew iosBuildDevice
```

#### List Available Simulators
```bash
./gradlew iosSimulatorList
```

## Android Studio Integration

### Gradle Panel Usage

1. Open Gradle panel (View → Tool Windows → Gradle)
2. Expand your project
3. Navigate to Tasks → ios development
4. Double-click any task to run

### External Tools Usage

1. Right-click anywhere in project
2. Select External Tools
3. Choose the appropriate tool
4. Output appears in the Run tool window

### Creating Run Configurations

You can create custom run configurations:

1. Run → Edit Configurations
2. Click "+" → Application (or Shell Script)
3. Configure:
   - **Name**: iOS Development
   - **Program**: `./gradlew`
   - **Arguments**: `iosRun`
   - **Working directory**: `$ProjectFileDir$`

## Debugging

### From Android Studio
- Set breakpoints in shared Kotlin code
- Debug shared module logic
- View variables in Kotlin/Native code

### From Xcode (for iOS-specific issues)
1. Open `iosApp/iosApp.xcodeproj` in Xcode
2. Set breakpoints in Swift code
3. Use Xcode's debugging tools for UI issues

## Troubleshooting

### Common Issues

1. **"xcodebuild command not found"**
   - Install Xcode from App Store
   - Run `sudo xcode-select --install`

2. **"No iOS simulators available"**
   - Open Xcode → Window → Devices and Simulators
   - Add simulators as needed

3. **"Framework not found"**
   - Run `./gradlew iosDev` to rebuild frameworks
   - Check framework paths in Xcode project

4. **"Simulator won't boot"**
   - Try different simulator
   - Reset simulator: Device → Erase All Content and Settings

### Debug Information

Run this to get complete environment info:
```bash
./gradlew iosDebugInfo
```

### Manual Cleanup

If builds fail, try manual cleanup:
```bash
# Clean Gradle cache
./gradlew clean

# Clean iOS build
./gradlew iosClean

# Reset simulator
xcrun simctl erase "iPhone 15"

# Full rebuild
./gradlew iosFullBuild
```

## File Locations

- **iOS Project**: `iosApp/iosApp.xcodeproj`
- **Shared Framework**: `shared/build/bin/iosSimulatorArm64/debugFramework/`
- **ComposeApp Framework**: `composeApp/build/bin/iosSimulatorArm64/debugFramework/`
- **iOS Build Output**: `iosApp/DerivedData/` (Xcode managed)

## Tips

1. **Fast Development**: Use `iosRunQuick` after first build
2. **Framework Changes**: Run `iosDev` when you modify shared code
3. **Simulator Issues**: Try `iosSimulatorBoot` to relaunch
4. **Build Issues**: Use `iosClean` then `iosDev`
5. **Environment Check**: Run `iosDebugInfo` when things go wrong

## Integration with KMM Plugin

If you have the Kotlin Multiplatform Mobile plugin installed:

1. The plugin may provide additional run configurations
2. Use the iOS app run configuration if available
3. Our Gradle tasks complement the plugin features

This setup gives you a complete iOS development workflow directly from Android Studio while maintaining the option to use Xcode when needed for iOS-specific tasks.