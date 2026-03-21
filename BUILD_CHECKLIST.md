# 📋 Build Configuration Checklist

## ✅ Pre-Build Verification

### Project Structure
- [ ] `app/build.gradle.kts` - Module build configuration
- [ ] `build.gradle.kts` - Root project configuration  
- [ ] `settings.gradle` - Project settings and modules
- [ ] `gradle/libs.versions.toml` - Version catalog
- [ ] `AndroidManifest.xml` - App manifest configuration
- [ ] `.github/workflows/android.yml` - GitHub Actions workflow

### Dependencies
- [ ] Wear OS Material Design 3 components
- [ ] Jetpack Compose for Wear OS
- [ ] Horologist UI components
- [ ] Navigation Component for Wear OS
- [ ] Testing libraries (JUnit, Robolectric, Roborazzi)

### Source Code
- [ ] `MainActivity.kt` - Main activity implementation
- [ ] Theme files in `presentation/theme/`
- [ ] Resource files (`strings.xml`, `colors.xml`, etc.)
- [ ] Drawable resources for icons
- [ ] Preview configurations

## 🔧 Build Configuration Details

### Android SDK Settings
```kotlin
// In app/build.gradle.kts
compileSdk = 36
minSdk = 26
targetSdk = 36
```

### Build Variants
- [ ] Debug build variant
- [ ] Release build variant
- [ ] ProGuard/R8 enabled for release builds

### Kotlin Configuration
- [ ] Kotlin version: 2.3.0
- [ ] JVM target: 17
- [ ] Compose compiler plugin configured

## ☁️ Cloud Build Setup

### GitHub Actions
- [ ] Workflow file created at `.github/workflows/android.yml`
- [ ] Java 17 setup configured
- [ ] Android SDK setup action included
- [ ] Gradle wrapper permissions set
- [ ] Dependency caching enabled
- [ ] Artifact upload configured

### Repository Configuration
- [ ] Repository is public (required for Actions)
- [ ] Branch protection rules set up
- [ ] Required status checks enabled
- [ ] Code owners configured

## 🧪 Testing Configuration

### Unit Tests
- [ ] JUnit test framework included
- [ ] Robolectric for Android testing
- [ ] Test resources properly configured

### UI Tests
- [ ] Compose UI testing libraries
- [ ] Test manifest configuration
- [ ] Preview annotations in place

### Screenshot Tests
- [ ] Roborazzi configured for screenshot testing
- [ ] Horologist screenshot utilities included

## 📱 Wear OS Specifics

### Manifest Configuration
- [ ] Wear OS hardware type detection
- [ ] Standalone app meta-data set
- [ ] Wake lock permission included
- [ ] Google Wear OS library required

### UI Components
- [ ] TransformingLazyColumn implemented
- [ ] EdgeButton for navigation
- [ ] ScreenScaffold with proper padding
- [ ] Responsive layouts for different watch sizes

### Performance
- [ ] Desugaring enabled for JDK 17 features
- [ ] Proper resource optimization
- [ ] Memory-efficient list implementations

## 🚀 Build Commands

### Local Build
```bash
# Clean previous builds
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK  
./gradlew assembleRelease

# Run all tests
./gradlew test

# Install on connected device
./gradlew installDebug
```

### Cloud Build
```yaml
# Automatically triggered by GitHub Actions
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { java-version: '17', distribution: 'temurin' }
      - uses: android-actions/setup-android@v3
      - run: chmod +x ./gradlew
      - run: ./gradlew assembleRelease --stacktrace
      - uses: actions/upload-artifact@v4
        with: { name: wear-os-apk, path: app/build/outputs/apk/release/ }
```

## 🔍 Post-Build Verification

### APK Analysis
- [ ] APK size within acceptable limits (< 15MB recommended)
- [ ] No unnecessary dependencies included
- [ ] ProGuard/R8 obfuscation working correctly

### Device Compatibility
- [ ] Works on round watch faces
- [ ] Works on square watch faces  
- [ ] Compatible with different DPI screens
- [ ] Touch interactions responsive

### Performance Metrics
- [ ] Launch time < 2 seconds
- [ ] Memory usage optimized
- [ ] Battery consumption reasonable
- [ ] Smooth animations and transitions

## 📝 Documentation

- [ ] README.md updated with project info
- [ ] CLOUD_BUILD.md added for cloud instructions
- [ ] QUICK_START.md for new developers
- [ ] BUILD_CHECKLIST.md for build verification
- [ ] License file included
- [ ] Contributing guidelines (if open source)

## 🎯 Success Criteria

When all checkboxes are completed:
- ✅ Project builds successfully both locally and in the cloud
- ✅ All tests pass
- ✅ APK installs and runs correctly on Wear OS devices
- ✅ Code follows Android/Wear OS best practices
- ✅ Documentation is complete and helpful

---

**Ready to build! 🎉**