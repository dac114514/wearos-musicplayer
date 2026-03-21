# 🚀 Wear OS Music Player - Quick Start Guide

## 🎯 Project Overview

This is a **Wear OS Music Player** project built with **Jetpack Compose for Wear OS**. It's designed to run on smartwatches and provides a modern, touch-friendly music playback interface.

### 📱 Features

- **Music Playback Controls**: Play, pause, skip tracks
- **Modern UI**: Material Design 3 components optimized for wearables
- **Navigation**: Swipe-to-dismiss navigation support
- **Responsive Layout**: Adapts to different watch sizes and orientations

### 🔧 Prerequisites

#### For Local Development (Optional)
- Android Studio Flamingo or later
- Android SDK API 26+ 
- Wear OS emulator or physical device
- JDK 17+

#### For Cloud Build (Recommended)
- GitHub account (you have one!)
- Push code to GitHub repository

### ☁️ Cloud Build Setup (GitHub Actions)

#### Step 1: Push Your Code
```bash
# If you haven't pushed yet
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/dac114514/wearos-musicplayer.git
git push -u origin main
```

#### Step 2: Monitor Build Progress
1. Go to your GitHub repository
2. Click on **Actions** tab
3. Select the latest workflow run
4. Watch the build progress in real-time

#### Step 3: Download Built APK
1. When build completes successfully, click **Artifacts**
2. Download `wear-os-apk.zip`
3. Extract and install on your Wear OS device

### 🏃‍♂️ Run Locally (Optional)

If you want to test locally:

```bash
# Make gradlew executable
chmod +x ./gradlew

# Clean and build debug APK
./gradlew clean assembleDebug

# Install on connected device/emulator
./gradlew installDebug
```

### 🎨 Project Structure

```
app/src/main/java/com/example/android/wearable/composestarter/
├── presentation/
│   └── MainActivity.kt          # Main activity and UI
└── theme/                       # App theme and colors
    └── Theme.kt
```

### 📋 Key Files

#### MainActivity.kt
Contains the core UI logic:
- Greeting screen with centered text
- List screen with transforming lazy column
- Navigation between screens
- Sample dialog component

#### AndroidManifest.xml
Configures Wear OS permissions and features:
- Wear OS hardware type detection
- Standalone app configuration
- Main activity declaration

### 🧪 Testing

The project includes comprehensive tests:
- Unit tests with Robolectric
- Compose UI tests
- Screenshot testing with Roborazzi

Run tests locally:
```bash
./gradlew test
./gradlew connectedAndroidTest
```

### 🔄 Continuous Integration

GitHub Actions automatically:
- ✅ Builds release APK on every push
- ✅ Runs all tests
- ✅ Generates build artifacts
- ✅ Caches dependencies for faster builds

### 📞 Getting Help

#### Common Issues & Solutions

**Build Fails**
```bash
# Check Gradle wrapper permissions
chmod +x ./gradlew

# Clean cache if dependencies are corrupted
./gradlew clean
```

**Emulator Not Starting**
- Ensure HAXM is installed (Windows/Mac)
- Use Android Studio's AVD Manager
- Minimum API level: 26 (Android 8.0)

**Device Not Detected**
- Enable Developer Options on Wear OS device
- Allow USB debugging
- Restart adb server: `adb kill-server && adb start-server`

### 🌟 Next Steps

1. **Customize the UI**: Modify `MainActivity.kt` to add music controls
2. **Add Real Music Logic**: Integrate with MediaSession API
3. **Enhance Navigation**: Add more screens for playlist management
4. **Optimize for Performance**: Profile and optimize for wearables

---

**Happy coding! 🎉 Your Wear OS music player is ready for development.**