import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
}

// iOS Development Tasks
tasks.register("iosDev") {
    group = "ios development"
    description = "Prepare iOS development environment - builds all frameworks"
    
    dependsOn(":shared:linkDebugFrameworkIosSimulatorArm64")
    dependsOn(":composeApp:linkDebugFrameworkIosSimulatorArm64")
    
    doLast {
        println("✅ iOS frameworks built successfully!")
        println("📱 You can now run the iOS app from Xcode or Android Studio")
        println("🔧 Frameworks location:")
        println("   - shared: shared/build/bin/iosSimulatorArm64/debugFramework/")
        println("   - composeApp: composeApp/build/bin/iosSimulatorArm64/debugFramework/")
    }
}

tasks.register("iosDevDevice") {
    group = "ios development"
    description = "Prepare iOS development environment for physical device"
    
    dependsOn(":shared:linkDebugFrameworkIosArm64")
    dependsOn(":composeApp:linkDebugFrameworkIosArm64")
    
    doLast {
        println("✅ iOS device frameworks built successfully!")
        println("📱 Ready for physical device deployment")
    }
}

tasks.register("iosClean") {
    group = "ios development"
    description = "Clean iOS build artifacts"
    
    doLast {
        exec {
            workingDir(file("iosApp"))
            commandLine("xcodebuild", "clean", "-project", "iosApp.xcodeproj", "-scheme", "iosApp")
        }
        delete(fileTree("shared/build/bin").matching { include("ios*/**") })
        delete(fileTree("composeApp/build/bin").matching { include("ios*/**") })
        println("🧹 iOS build artifacts cleaned")
    }
}

tasks.register("iosBuild") {
    group = "ios development"
    description = "Build iOS app for simulator"
    
    dependsOn("iosDev")
    
    doLast {
        exec {
            workingDir(file("iosApp"))
            commandLine(
                "xcodebuild", 
                "-project", "iosApp.xcodeproj",
                "-scheme", "iosApp",
                "-destination", "platform=iOS Simulator,id=E650E5F0-F569-4AF7-A298-661705E456E1",
                "build"
            )
        }
        println("🔨 iOS app built successfully for simulator")
    }
}

tasks.register("iosBuildDevice") {
    group = "ios development"
    description = "Build iOS app for physical device"
    
    dependsOn("iosDevDevice")
    
    doLast {
        exec {
            workingDir(file("iosApp"))
            commandLine(
                "xcodebuild", 
                "-project", "iosApp.xcodeproj",
                "-scheme", "iosApp",
                "-destination", "generic/platform=iOS",
                "build"
            )
        }
        println("🔨 iOS app built successfully for device")
    }
}

tasks.register("iosSimulatorList") {
    group = "ios development"
    description = "List available iOS simulators"
    
    doLast {
        exec {
            commandLine("xcrun", "simctl", "list", "devices", "available")
        }
    }
}

tasks.register("iosSimulatorBoot") {
    group = "ios development"
    description = "Boot iPhone 15 simulator"
    
    doLast {
        try {
            exec {
                commandLine("xcrun", "simctl", "boot", "E650E5F0-F569-4AF7-A298-661705E456E1")
            }
        } catch (e: Exception) {
            println("📱 Simulator might already be booted: ${e.message}")
        }
        
        exec {
            commandLine("open", "-a", "Simulator")
        }
        println("📱 iPhone 15 simulator launched")
    }
}

tasks.register("iosRun") {
    group = "ios development"
    description = "Build and run iOS app in simulator"
    
    dependsOn("iosBuild")
    dependsOn("iosSimulatorBoot")
    
    doLast {
        println("🚀 iOS app should be running in simulator")
        println("💡 If the app doesn't launch automatically, run it manually from the simulator")
    }
}

tasks.register("iosRunQuick") {
    group = "ios development"
    description = "Quick run - assumes frameworks are already built"
    
    doLast {
        // Boot simulator in parallel
        exec {
            commandLine("xcrun", "simctl", "boot", "E650E5F0-F569-4AF7-A298-661705E456E1")
            isIgnoreExitValue = true
        }
        
        // Build app
        exec {
            workingDir(file("iosApp"))
            commandLine(
                "xcodebuild", 
                "-project", "iosApp.xcodeproj",
                "-scheme", "iosApp",
                "-destination", "platform=iOS Simulator,id=E650E5F0-F569-4AF7-A298-661705E456E1",
                "build"
            )
        }
        
        // Install app to simulator
        val appPath = file("${System.getProperty("user.home")}/Library/Developer/Xcode/DerivedData").listFiles()
            ?.find { it.name.startsWith("iosApp-") }
            ?.resolve("Build/Products/Debug-iphonesimulator/iosApp.app")
        
        if (appPath?.exists() == true) {
            try {
                exec {
                    commandLine("xcrun", "simctl", "uninstall", "E650E5F0-F569-4AF7-A298-661705E456E1", "com.markduenas.pigstally")
                    isIgnoreExitValue = true
                }
                exec {
                    commandLine("xcrun", "simctl", "install", "E650E5F0-F569-4AF7-A298-661705E456E1", appPath.absolutePath)
                }
                exec {
                    commandLine("xcrun", "simctl", "launch", "E650E5F0-F569-4AF7-A298-661705E456E1", "com.markduenas.pigstally")
                }
                println("📱 App installed and launched successfully")
            } catch (e: Exception) {
                println("⚠️ App built but installation failed: ${e.message}")
            }
        } else {
            println("⚠️ App bundle not found - app built but not installed")
        }
        
        exec {
            commandLine("open", "-a", "Simulator")
        }
        
        println("🚀 iOS app process completed")
    }
}

tasks.register("iosFullBuild") {
    group = "ios development"
    description = "Complete build - clean, build frameworks, build app"
    
    dependsOn("iosClean")
    dependsOn("iosBuild")
    
    tasks.findByName("iosBuild")?.mustRunAfter("iosClean")
    
    doLast {
        println("✅ Complete iOS build finished")
    }
}

tasks.register("iosDebugInfo") {
    group = "ios development"
    description = "Show iOS development environment info"
    
    doLast {
        println("🔍 iOS Development Environment Info:")
        println("----------------------------------------")
        
        // Xcode version
        try {
            val xcodeBuild = ByteArrayOutputStream()
            exec {
                commandLine("xcodebuild", "-version")
                standardOutput = xcodeBuild
            }
            println("Xcode Version:")
            println(xcodeBuild.toString().trim())
        } catch (e: Exception) {
            println("❌ Xcode not found or not accessible")
        }
        
        println("\n📱 Available iOS Simulators:")
        try {
            exec {
                commandLine("xcrun", "simctl", "list", "devices", "iPhone", "--json")
            }
        } catch (e: Exception) {
            println("❌ Could not list simulators")
        }
        
        println("\n🏗️ Framework Status:")
        val sharedFramework = file("shared/build/bin/iosSimulatorArm64/debugFramework/shared.framework")
        val composeFramework = file("composeApp/build/bin/iosSimulatorArm64/debugFramework/ComposeApp.framework")
        
        println("Shared Framework: ${if (sharedFramework.exists()) "✅ Built" else "❌ Missing"}")
        println("ComposeApp Framework: ${if (composeFramework.exists()) "✅ Built" else "❌ Missing"}")
    }
}